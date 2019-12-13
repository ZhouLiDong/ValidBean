package com.bean.valid.util;


import com.bean.valid.exption.ParamException;
import com.bean.valid.exption.ValidatedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;

/**
 * 校验对象的指定方法返回值
 *
 * @Author 周利东
 * @Date: 2019/9/4 14:40
 */
public class ValidBean<T> {

    /**
     * 非空的校验函数与对应默认错误信息
     */
    private final static ValidPair<Object> needNotEmptyValidPair = new ValidPair() {
        {
            /**
             * 需要非空值，如果被校验值是空值，则返回false，反之返回true
             */
            validFunction = validValue -> {

                if (validValue == null) {
                    return false;
                }

                if (validValue instanceof String) {
                    if (StringUtils.isBlank(validValue.toString())) {
                        return false;
                    }
                }

                if (validValue instanceof Collection) {
                    if (CollectionUtils.isEmpty((Collection) validValue)) {
                        return false;
                    }
                }

                if (validValue instanceof Map) {
                    if (((Map) validValue).isEmpty()) {
                        return false;
                    }
                }

                return true;
            };

            errorMsg = "属性不能为空";
        }
    };


    /**
     * 需要为空的校验函数与对应默认错误信息
     */
    private final static ValidPair<Object> needEmptyValidPair = new ValidPair() {
        {
            /**
             * 需要空值，如果被校验值是空值，则返回true，反之返回false
             */
            validFunction = validValue -> !needNotEmptyValidPair.validFunction.apply(validValue);

            errorMsg = "属性需要为空";
        }
    };

    /**
     * 被校验的对象
     */
    private T bean;

    /**
     * 是否延迟校验。执行valid方法调用链过程中可以通过lazy与immediately方法可以修改，修改后立即生效
     * （true: 等调用complete方法时全部一起校验。false: 调用valid方法时立马校验）
     */
    private boolean lazyEnable;

    /**
     * 是否使用默认错误消息（无自定义错误消息时使用默认的）
     */
    private boolean useDefaultErrorMsgEnable;

    /**
     * 延迟处理的错误消息列表
     */
    private List<String> lazyErrorMsgList;

    /**
     * 默认校验对
     */
    private ValidPair<Object> defaultValidPair = needNotEmptyValidPair;

    /**
     * 待校验的校验数据列表
     */
    private List<ValidTriple<Object>> validTripleList;


    /**
     * 校验需要的三部分相关数据
     *
     * @param <V> 被校验的值类型
     */
    static class ValidTriple<V> {
        /**
         * 被校验的值
         */
        V validValue;

        /**
         * 校验函数（校验validValue字段值的函数）
         */
        Function<V, Boolean> validFunction;

        /**
         * 当validFunction校验未通过时的错误信息
         */
        String errorMsg;


        ValidTriple(V validValue, Function<V, Boolean> validFunction) {
            this.validValue = validValue;
            this.validFunction = validFunction;
        }

        ValidTriple(V validValue, Function<V, Boolean> validFunction, String errorMsg) {
            this.validValue = validValue;
            this.validFunction = validFunction;
            this.errorMsg = errorMsg;
        }
    }

    /**
     * 校验函数与错误信息 对
     *
     * @param <V> 被校验的值的类型
     */
    static class ValidPair<V> {
        /**
         * 校验函数
         */
        ValidFunction<V> validFunction;

        /**
         * 校验函数未通过时的错误信息
         */
        String errorMsg;

        public ValidPair() {
        }

        public ValidPair(ValidFunction<V> validFunction, String errorMsg) {
            this.validFunction = validFunction;
            this.errorMsg = errorMsg;
        }
    }


    /**
     * 校验函数
     *
     * @param <P>
     */
    @FunctionalInterface
    public interface ValidFunction<P> extends Function<P, Boolean> {

        /**
         * @param valid 被校验的值
         * @return true:校验通过。false:校验未通过
         */
        @Override
        Boolean apply(P valid);
    }

    ValidBean(T bean, boolean lazyEnable) {
        if (bean == null) {
            throw new ParamException("参数不能为空");
        }
        this.bean = bean;

        this.lazyEnable = lazyEnable;
    }

    T getBean() {
        return bean;
    }

    /**
     * 获取父ValidBean,该方法只有ValidSubBean才有用，只有ValidSubBean才有父ValidBean
     *
     * @param <PT> 父类型的泛型类型
     * @return
     */
    public <PT> ValidBean<PT> parent() {
        throw new ValidatedException("当前不是ValidSubBean，没有parent");
    }


    /**
     * 转换为校验另一个对象的ValidBean
     *
     * @param mapper
     * @param <ST>
     * @return
     */
    public <ST> ValidSubBean<ST, T> map(Function<T, ST> mapper) {
        Objects.requireNonNull(mapper);
        ST subParamBean = mapper.apply(bean);
        if (subParamBean == null) {
            throw new ParamException("被校验的子属性不能为空");
        }
        return new ValidSubBean<>(this, subParamBean, isLazy());
    }

    /**
     * 指定方法引用返回值需要为空
     *
     * @param methodRef 方法引用
     * @return
     */
    public ValidBean<T> empty(Function<T, Object> methodRef) {
        return valid(methodRef, needEmptyValidPair.validFunction, needEmptyValidPair.errorMsg);
    }

    /**
     * 指定方法引用返回值需要为空
     *
     * @param methodRef      方法引用
     * @param customErrorMsg 自定义错误信息
     * @return
     */
    public ValidBean<T> empty(Function<T, Object> methodRef, String customErrorMsg) {
        return valid(methodRef, needEmptyValidPair.validFunction, customErrorMsg);
    }

    protected boolean isLazy() {
        return lazyEnable;
    }

    /**
     * 指定方法引用返回值不能为空
     *
     * @param methodRef 方法引用
     * @return
     */
    public ValidBean<T> notEmpty(Function<T, Object> methodRef) {
        return valid(methodRef, needNotEmptyValidPair.validFunction, needNotEmptyValidPair.errorMsg);
    }

    /**
     * 指定方法引用返回值不能为空
     *
     * @param methodRef      方法引用
     * @param customErrorMsg 自定义错误信息
     * @return
     */
    public ValidBean<T> notEmpty(Function<T, Object> methodRef, String customErrorMsg) {
        return valid(methodRef, needNotEmptyValidPair.validFunction, customErrorMsg);
    }


    /**
     * 校验paramBean的指定方法返回值
     *
     * @param methodRef 被校验的bean的方法引用，通过该函数获取要被校验的值
     * @return
     */
    public ValidBean<T> valid(Function<T, Object> methodRef) {
        return valid(methodRef, null, null);
    }


    /**
     * 校验paramBean的指定方法返回值(使用默认校验函数)
     *
     * @param methodRef 被校验的bean的方法引用，通过该函数获取要被校验的值
     * @param errorMsg  自定义错误信息，未通过校验时使用该自定义错误信息(如果为空则使用默认错误信息)
     * @return
     */
    public ValidBean<T> valid(Function<T, Object> methodRef, String errorMsg) {
        return valid(methodRef, null, errorMsg);
    }


    /**
     * 校验指定方法返回值
     *
     * @param methodRef     被校验的bean的方法引用，通过该函数获取要被校验的值
     * @param validFunction 自定义校验函数，仅校验当前该步骤指定的方法引用返回值，其它valid还是使用全局校验函数进行校验
     * @param errorMsg      自定义错误信息(如果为空则使用默认错误信息)
     * @return
     */
    public <R> ValidBean<T> valid(Function<T, R> methodRef, ValidFunction<R> validFunction, String errorMsg) {
        Objects.requireNonNull(methodRef);

        //获取被校验的值
        R validValue = methodRef.apply(bean);

        ValidTriple validTriple = new ValidTriple<R>(validValue, validFunction, errorMsg);

        //添加到待校验列表
        getValidTripleList().add(validTriple);

        if (!isLazy()) {

            //立即校验
            immediatelyValid();
        }
        return this;
    }


    /**
     * 立即执行校验，并抛出错误信息
     */
    private <R> void immediatelyValid() {

        //校验值并添加错误信息
        getValidTripleList().forEach(this::validValueAndAddErrorMsg);

        //如果存在错误信息，则立即抛出异常
        throwExceptionIfExitsErrorMsg();
    }

    /**
     * 默认的错误提示信息
     *
     * @return
     */
    protected String getDefaultErrorMsg() {
        return defaultValidPair.errorMsg;
    }

    protected List<String> getLazyErrorMsgList() {
        if (lazyErrorMsgList == null) {
            lazyErrorMsgList = new ArrayList<>();
        }

        return lazyErrorMsgList;
    }

    /**
     * 如果存在错误信息，则立即抛出异常
     */
    protected void throwExceptionIfExitsErrorMsg() {

        StringJoiner joiner = new StringJoiner(",");

        //默认错误信息放在开头
        if (useDefaultErrorMsgEnable) {
            joiner.add(defaultValidPair.errorMsg);
        }

        List<String> lazyErrorMsgList = getLazyErrorMsgList();
        if (CollectionUtils.isNotEmpty(lazyErrorMsgList)) {
            lazyErrorMsgList.forEach(joiner::add);
        }

        //存在错误信息时抛出异常
        String allErrorMsg = joiner.toString();
        if (StringUtils.isNotBlank(allErrorMsg)) {
            throw new ParamException(allErrorMsg);
        }
    }

    public void complete() {
        if (isLazy()) {

            immediatelyValid();
        }
    }

    /**
     * ValidBean执行链中是否存在一个及其以上使用默认错误提示信息的
     *
     * @return
     */
    protected boolean getUseDefaultErrorMsgEnableForChain() {
        return useDefaultErrorMsgEnable;
    }


    /**
     * 当前ValidBean是否使用默认错误提示信息
     *
     * @return
     */
    protected boolean getUseDefaultErrorMsgEnable() {
        return useDefaultErrorMsgEnable;
    }

    protected void setValidTripleList(List<ValidTriple<Object>> validTripleList) {
        this.validTripleList = validTripleList;
    }

    /**
     * 获取自身ValidTripleList
     *
     * @return
     */
    public List<ValidTriple<Object>> getValidTripleList() {
        if (validTripleList == null) {
            validTripleList = new ArrayList<>();
        }
        return validTripleList;
    }

    /**
     * 校验值且记录一下错误信息
     *
     * @param validTriple 执行校验的数据
     */
    protected <R> void validValueAndAddErrorMsg(ValidTriple<R> validTriple) {
        Function<R, Boolean> validFunction = validTriple.validFunction;
        R validValue = validTriple.validValue;


        //是否指定校验函数，如果没指定则使用全局校验函数
        Boolean isPass = (validFunction == null)
                ? defaultValidPair.validFunction.apply(validValue)  //使用默认的校验函数
                : validFunction.apply(validValue);  //使用指定的校验函数

        if (isPass == null) {
            throw new IllegalArgumentException("校验函数返回值不能为null");
        }

        //未通过校验则记录错误信息
        if (!isPass) {

            if (validTriple.validFunction == defaultValidPair.validFunction && defaultValidPair.errorMsg.equals(validTriple.errorMsg)) {
                //使用默认错误提示
                addErrorMsg(null);
            } else {
                addErrorMsg(validTriple.errorMsg);
            }
        }
    }


    /**
     * 添加错误信息
     *
     * @param errorMsg 错误提示信息，为空时表示当前ValidBean会使用默认错误信息
     */
    private void addErrorMsg(String errorMsg) {
        if (StringUtils.isBlank(errorMsg)) {

            //使用默认错误信息
            useDefaultErrorMsgEnable = true;
        } else {
            List<String> lazyErrorMsgList = getLazyErrorMsgList();

            lazyErrorMsgList.add(errorMsg);
        }
    }

    protected void setLazyErrorMsgList(List<String> lazyErrorMsgList) {
        this.lazyErrorMsgList = lazyErrorMsgList;
    }

    /**
     * 设置全局校验函数和全局错误信息
     *
     * @param customValidFunction
     * @param defaultErrorMsg
     */
    public void setGlobalValidPair(ValidFunction<Object> customValidFunction, String defaultErrorMsg) {
        this.defaultValidPair = new ValidPair<>(customValidFunction, defaultErrorMsg);
    }

    /**
     * 设置全局校验函数和全局错误信息
     *
     * @param customValidPair
     */
    public void setGlobalValidPair(ValidPair<Object> customValidPair) {
        Objects.requireNonNull(customValidPair);
        Objects.requireNonNull(customValidPair.validFunction, "设置的全局校验函数不能为空");

        if (StringUtils.isBlank(customValidPair.errorMsg)) {
            throw new NullPointerException("设置的全局错误信息不能为空");
        }

        this.defaultValidPair = customValidPair;
    }

    /**
     * 设置校验模式为延迟校验。
     *
     * @return
     */
    public ValidBean<T> lazy() {
        lazyEnable = true;
        return this;
    }

    /**
     * 设置校验模式为立即校验，如果当前存在延迟校验的数据时，则把这些数据都先校验完。
     *
     * @return
     */
    public ValidBean<T> immediately() {
        immediatelyValid();
        lazyEnable = false;
        return this;
    }

    /**
     * 创建validBean实例，默认延迟校验，调用complete方法时才会执行校验，不符合校验则抛出异常
     *
     * @param bean
     * @return
     */
    public static <P> ValidBean<P> ofLazy(P bean) {
        return new ValidBean(bean, true);
    }

    /**
     * 创建validBean实例，默认立即校验，每次调用valid方法都会立即校验，如果不符合校验则立即抛出异常
     *
     * @param bean
     * @return
     */
    public static <P> ValidBean<P> of(P bean) {
        return new ValidBean(bean, false);
    }


}
