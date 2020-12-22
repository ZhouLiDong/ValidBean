package com.bean.valid.util;


import com.bean.valid.exption.ValidatedException;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Function;

import static com.bean.valid.util.ValidConstant.defaultGlobalExConstructor;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.collections4.CollectionUtils.*;

/**
 * 校验对象的指定方法返回值
 *
 * @Author 周利东
 * @Date: 2019/9/4 14:40
 */
public abstract class AbstractEntityValid<E> {

    /**
     * 是否延迟校验。执行valid方法调用链过程中可以通过lazy与immediately方法可以修改，修改后立即生效
     * （true: 等调用complete方法时全部一起校验。false: 调用valid方法时立马校验）
     */
    private boolean lazyEnable;

    /**
     * 被校验的对象
     */
    private final E entity;

    /**
     * 默认异常构造方法（单个参数，并且是String类型）
     */
    private Constructor<? extends RuntimeException> defaultExcConstructor = defaultGlobalExConstructor;


    AbstractEntityValid(E entity, boolean lazyEnable) {
        if (entity == null) {
            throw defaultException("entity不能为空");
        }
        this.entity = entity;
        this.lazyEnable = lazyEnable;
    }

    /**
     * 获取异常对象实例
     *
     * @param msg
     * @return
     */
    private RuntimeException defaultException(String msg) {
        try {
            return defaultExcConstructor.newInstance(msg);
        } catch (Exception e) {
            throw new ValidatedException("默认异常没有String类型参数的构造函数，请设置符合条件的默认异常！", e);
        }
    }

    protected boolean isLazy() {
        return lazyEnable;
    }

    public void setLazyEnable(boolean lazyEnable) {
        this.lazyEnable = lazyEnable;

    }

    E getEntity() {
        return entity;
    }

    /**
     * 设置默认异常
     *
     * @param defaultException
     * @return
     */
    public AbstractEntityValid<E> defaultException(Class<? extends RuntimeException> defaultException) {
        try {
            this.defaultExcConstructor = defaultException.getConstructor(String.class);
        } catch (Exception e) {
            throw new ValidatedException(defaultException.getName() + "异常没有String类型参数的构造函数，请设置符合条件的默认异常！", e);
        }
        return this;
    }

    /**
     * 非空的校验函数与对应默认错误信息
     */
    private final static ValidPair<Object> needNotEmptyValidPair = new ValidPair() {
        {
            /**
             * 需要非空值，如果被校验值是空值，则返回false，反之返回true
             */
            ruleFunction = validValue -> {

                if (validValue == null) {
                    return false;
                }

                if (validValue instanceof String) {
                    if (isBlank(validValue.toString())) {
                        return false;
                    }
                }

                if (validValue instanceof Collection) {
                    if (isEmpty((Collection) validValue)) {
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
            ruleFunction = validValue -> !needNotEmptyValidPair.ruleFunction.apply(validValue);

            errorMsg = "属性需要为空";
        }
    };

    /**
     * 是否使用默认错误消息（无自定义错误消息时使用默认的）
     */
    private boolean useDefaultErrorMsgEnable;

    /**
     * 延迟处理的错误消息列表
     */
    private List<String> lazyErrorMsgList;


    /**
     * 待校验的校验数据列表
     */
    private List<RuleConfig<? extends Object, ? extends Object>> ruleConfigList;

    /**
     * 默认错误信息
     */
    private String defaultErrorMsg = ValidConstant.defaultErrorMsg;

    /**
     * 校验函数与错误信息 对
     *
     * @param <V> 被校验的值的类型
     */
    static class ValidPair<V> {
        /**
         * 校验函数
         */
        RuleFunction<V> ruleFunction;

        /**
         * 校验函数未通过时的错误信息
         */
        String errorMsg;

        public ValidPair() {
        }

        public ValidPair(RuleFunction<V> ruleFunction, String errorMsg) {
            this.ruleFunction = ruleFunction;
            this.errorMsg = errorMsg;
        }

        public RuleFunction<V> getRuleFunction() {
            return ruleFunction;
        }

        public void setRuleFunction(RuleFunction<V> ruleFunction) {
            this.ruleFunction = ruleFunction;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }

    /**
     * 获取父Bean,该方法只有SubBean才有用，只有SubBean才有父Bean
     *
     * @param <PT> 父类型的泛型类型
     * @return
     */
    public <PT> AbstractEntityValid<PT> parent() {
        throw defaultException("当前已经是最顶级的Bean,没有parent");
    }

    /**
     * 强制转换普通类型
     *
     * @param <TT>
     * @return
     */
    public <TT> AbstractEntityValid<TT> parentAndCast(TypeReference<TT> typeReference) {
        throw defaultException("当前已经是最顶级的Bean,没有parent");
    }

    /**
     * 强制转换泛型类型
     *
     * @param <TT>
     * @return
     */
    public <TT> AbstractEntityValid<TT> cast(TypeReference<TT> typeReference) {
        return (AbstractEntityValid<TT>) this;
    }

    /**
     * 强制转换普通类型
     *
     * @param <TT>
     * @return
     */
    public <TT> AbstractEntityValid<TT> cast(Class<TT> ttClass) {
        return (AbstractEntityValid<TT>) this;
    }


    /**
     * 指定方法引用返回值需要为空
     *
     * @param fieldFunc 方法引用
     * @return
     */
    public EntityValid<E> empty(Function<E, Object> fieldFunc) {
        return valid(fieldFunc, needEmptyValidPair.ruleFunction, needEmptyValidPair.errorMsg);
    }

    /**
     * 指定方法引用返回值需要为空
     *
     * @param fieldFunc      方法引用
     * @param customErrorMsg 自定义错误信息
     * @return
     */
    public EntityValid<E> empty(Function<E, Object> fieldFunc, String customErrorMsg) {
        return valid(fieldFunc, needEmptyValidPair.ruleFunction, customErrorMsg);
    }


    /**
     * 指定方法引用返回值不能为空
     *
     * @param fieldFunc 方法引用
     * @return
     */
    public EntityValid<E> notEmpty(Function<E, Object> fieldFunc) {
        return notEmpty(fieldFunc, needNotEmptyValidPair.errorMsg);
    }

    /**
     * 指定方法引用返回值不能为空
     *
     * @param fieldFunc      方法引用
     * @param customErrorMsg 自定义错误信息
     * @return
     */
    public EntityValid<E> notEmpty(Function<E, Object> fieldFunc, String customErrorMsg) {
        return valid(fieldFunc, customErrorMsg);
    }


    /**
     * 校验paramBean的指定方法返回值
     *
     * @param fieldFunc 被校验的bean的方法引用，通过该函数获取要被校验的值
     * @return
     */
    public EntityValid<E> valid(Function<E, Object> fieldFunc) {
        return valid(fieldFunc, null);
    }


    /**
     * 校验paramBean的指定方法返回值(使用默认校验函数)
     *
     * @param fieldFunc 被校验的bean的方法引用，通过该函数获取要被校验的值
     * @param errorMsg  自定义错误信息，未通过校验时使用该自定义错误信息(如果为空则使用默认错误信息)
     * @return
     */
    public EntityValid<E> valid(Function<E, Object> fieldFunc, String errorMsg) {
        return valid(fieldFunc, needNotEmptyValidPair.ruleFunction, errorMsg);
    }


    /**
     * 校验指定方法返回值
     *
     * @param fieldFunc     被校验的bean的方法引用，通过该函数获取要被校验的值
     * @param ruleFunction 自定义校验函数，仅校验当前该步骤指定的方法引用返回值，其它valid还是使用全局校验函数进行校验
     * @param errorMsg      自定义错误信息(如果为空则使用默认错误信息)
     * @return
     */
    public <R> EntityValid<E> valid(Function<E, R> fieldFunc, RuleFunction<R> ruleFunction, String errorMsg) {
        Objects.requireNonNull(fieldFunc);
        if (ruleFunction == null) {
            ruleFunction = (RuleFunction<R>) needNotEmptyValidPair.ruleFunction;
        }


        addValidConfig(new RuleConfig<E, R>(entity, fieldFunc, ruleFunction, errorMsg));

        //立即校验
        if (!isLazy()) {
            immediatelyValid();
        }
        return new EntityValid<>(this.getEntity(), isLazy());
    }

    /**
     * 添加校验配置
     *
     * @param ruleConfig
     * @return
     */
    protected boolean addValidConfig(RuleConfig ruleConfig) {
        return getRuleConfigList().add(ruleConfig);
    }


    /**
     * 默认的错误提示信息
     *
     * @return
     */
    protected String getDefaultErrorMsg() {
        return this.defaultErrorMsg;
    }

    protected List<String> getLazyErrorMsgList() {
        if (lazyErrorMsgList == null) {
            lazyErrorMsgList = new ArrayList<>();
        }

        return lazyErrorMsgList;
    }


    public void complete() {
        if (isLazy()) {

            immediatelyValid();
        }
    }

    /**
     * Bean执行链中是否存在一个及其以上使用默认错误提示信息的
     *
     * @return
     */
    protected boolean getUseDefaultErrorMsgEnableForChain() {
        return useDefaultErrorMsgEnable;
    }


    /**
     * 当前Bean是否使用默认错误提示信息
     *
     * @return
     */
    protected boolean getUseDefaultErrorMsgEnable() {
        return useDefaultErrorMsgEnable;
    }


    /**
     * 获取自身ValidTripleList
     *
     * @return
     */
    public List<RuleConfig<? extends Object, ? extends Object>> getRuleConfigList() {
        if (ruleConfigList == null) {
            ruleConfigList = new LinkedList<>();
        }
        return ruleConfigList;
    }

    /**
     * 校验值且记录一下错误信息
     *
     * @param ruleConfig 执行校验的数据
     */
    protected <R> void validValueAndAddErrorMsg(RuleConfig<? extends Object, ? extends Object> ruleConfig) {
        RuleExecutor<?, ?> ruleExecutor = RuleExecutor.create(ruleConfig);
        //未通过校验则记录错误信息
        if (ruleExecutor.execRule()) {
            useDefaultErrorMsgEnable = true;
        }else {
            addErrorMsg(ruleExecutor.getErrorMsg());
        }
    }


    /**
     * 添加错误信息
     *
     * @param errorMsg 错误提示信息，为空时表示当前Bean会使用默认错误信息
     */
    private void addErrorMsg(String errorMsg) {
        List<String> lazyErrorMsgList = getLazyErrorMsgList();
        lazyErrorMsgList.add(isBlank(errorMsg) ? getDefaultErrorMsg() : errorMsg);
    }

    protected void setLazyErrorMsgList(List<String> lazyErrorMsgList) {
        this.lazyErrorMsgList = lazyErrorMsgList;
    }

    /**
     * 设置默认错误信息
     *
     * @param defaultErrorMsg
     */
    public void setDefaultErrorMsg(String defaultErrorMsg) {
        this.defaultErrorMsg = defaultErrorMsg;
    }

    /**
     * 设置校验模式为延迟校验。
     *
     * @return
     */
    public EntityValid<E> lazy() {
        setLazyEnable(true);
        return new EntityValid<>(this.getEntity(), isLazy());
    }

    /**
     * 设置校验模式为立即校验，如果当前存在延迟校验的数据时，则把这些数据都先校验完。
     *
     * @return
     */
    public EntityValid<E> immediately() {
        immediatelyValid();
        setLazyEnable(false);
        return new EntityValid<E>(this.getEntity(), isLazy());
    }


    /**
     * 立即执行校验，并抛出错误信息
     */
    <R> void immediatelyValid() {

        Iterator<RuleConfig<?, ?>> iterator = getRuleConfigList().iterator();
        while (iterator.hasNext()){
            //校验值并添加错误信息
           this.validValueAndAddErrorMsg( iterator.next());
           //已校验过的后续不再重复校验
           iterator.remove();
        }

        //如果存在错误信息，则立即抛出异常
        throwExceptionIfExitsErrorMsg();
    }

    /**
     * 如果存在错误信息，则立即抛出异常
     */
    protected void throwExceptionIfExitsErrorMsg() {

        StringJoiner joiner = new StringJoiner(",");

        //默认错误信息放在开头

        List<String> lazyErrorMsgList = getLazyErrorMsgList();
        if (isNotEmpty(lazyErrorMsgList)) {
            lazyErrorMsgList.forEach(joiner::add);
        }

        //存在错误信息时抛出异常
        String allErrorMsg = joiner.toString();
        if (isNotBlank(allErrorMsg)) {
            throw defaultException(allErrorMsg);
        }
    }
    /**
     * 创建Bean实例，默认延迟校验，调用complete方法时才会执行校验，不符合校验则抛出异常
     *
     * @param bean
     * @return
     */
    public static <P> EntityValid<P> ofLazy(P bean) {
        return new EntityValid(bean, true);
    }

    /**
     * 创建Bean实例，默认立即校验，每次调用valid方法都会立即校验，如果不符合校验则立即抛出异常
     *
     * @param bean
     * @return
     */
    public static <P> EntityValid<P> of(P bean) {
        return new EntityValid<>(bean, false);
    }


}
