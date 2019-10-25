package com.bean.valid.util;

import com.bean.valid.exption.ParamException;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 通过ValidBean的map方法，创建本类对PT类型的对象的指定方法引用返回的对象实例(ST类型)进行校验
 *
 * @param <ST> 被校验的子类型
 * @param <PT> 被校验的父类型
 */
public class ValidSubBean<ST, PT> extends ValidBean<ST> {
    private ValidBean<PT> parentValidBean;

    ValidSubBean(ValidBean<PT> parentValidBean, ST paramBean, boolean lazyEnable) {
        super(paramBean, lazyEnable);

        if (parentValidBean == null) {
            throw new ParamException("父校验bean不能为空");
        }
        this.parentValidBean = parentValidBean;

    }

    /**
     * 跳转到父属性校验
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> ValidBean<T> parent(TypeReference<T> typeReference) {
        return new ValidSubBean<T, ST>(this, (T) parentValidBean.getBean(), isLazy());
    }

    /**
     * 获取父链的ValidTriple列表(不包含本身ValidSubBean)
     *
     * @return
     */
    public List<ValidTriple<Object>> getValidTripleListForParentChain() {
        List<ValidTriple<Object>> list = new ArrayList<>();

        //父链=父+祖宗链

        //祖宗链
        if (parentValidBean instanceof ValidSubBean) {
            list.addAll(((ValidSubBean) parentValidBean).getValidTripleListForParentChain());
        }

        //父
        list.addAll(parentValidBean.getValidTripleList());

        return list;
    }

    @Override
    public void complete() {
        //执行校验并添加错误信息

        //执行父链校验函数
        if (parentValidBean.isLazy()) {
            getValidTripleListForParentChain().forEach(this::validValueAndAddErrorMsg);
        }

        //执行当前ValidBean校验函数
        if (isLazy()) {
            getValidTripleList().forEach(this::validValueAndAddErrorMsg);
        }

        if (parentValidBean.isLazy() || isLazy()) {

            //存在错误信息时抛出异常
            throwExceptionIfExitsErrorMsg();
        }
    }

    /**
     * ValidBean执行链中是否存在一个及其以上使用默认提示信息的
     *
     * @return
     */
    @Override
    protected boolean getUseDefaultErrorMsgEnableForChain() {
        //父与子是否使用了默认错误提示信息
        return parentValidBean.getUseDefaultErrorMsgEnableForChain() || getUseDefaultErrorMsgEnable();
    }

    /**
     * 获取所有默认的错误提示信息
     *
     * @return
     */
    private Set<String> getAllDefaultErrorMsg() {
        Set<String> set = new LinkedHashSet<>();

        //父ValidBean调用链中是否存在使用默认错误提示信息的
        boolean defaultErrorMsgEnableForParentChain = parentValidBean.getUseDefaultErrorMsgEnableForChain();

        //当前ValidBean是否存在使用默认错误提示信息的
        boolean currentDefaultErrorMsgEnable = getUseDefaultErrorMsgEnable();

        //父链与子都使用了默认错误提示信息
        if (defaultErrorMsgEnableForParentChain && currentDefaultErrorMsgEnable) {

            //添加父校验的默认错误信息
            if (parentValidBean instanceof ValidSubBean) {
                set.addAll(((ValidSubBean) parentValidBean).getAllDefaultErrorMsg());
            } else {
                set.add(parentValidBean.getDefaultErrorMsg());
            }
            //添加子校验的默认错误信息
            set.add(getDefaultErrorMsg());
        }

        //仅父链使用了默认错误提示
        if (defaultErrorMsgEnableForParentChain && !currentDefaultErrorMsgEnable) {
            if (parentValidBean instanceof ValidSubBean) {
                set.addAll(((ValidSubBean) parentValidBean).getAllDefaultErrorMsg());
            } else {
                set.add(parentValidBean.getDefaultErrorMsg());
            }
        }

        //仅当前校验使用了默认错误提示
        if (!defaultErrorMsgEnableForParentChain && currentDefaultErrorMsgEnable) {
            set.add(getDefaultErrorMsg());
        }
        return set;
    }

//    /**
//     * 获取父链的错误信息列表
//     *
//     * @return
//     */
//    protected List<String> getLazyErrorMsgListForParentChain() {
//        List<String> list = new ArrayList<>();
//
//        list.addAll(parentValidBean.getLazyErrorMsgList());
//        if (parentValidBean instanceof ValidSubBean) {
//            list.addAll(((ValidSubBean) parentValidBean).getLazyErrorMsgListForParentChain());
//        }
//
//        return list;
//    }

    @Override
    protected void throwExceptionIfExitsErrorMsg() {
        StringJoiner joiner = new StringJoiner(",");

        //默认错误消息
        getAllDefaultErrorMsg().forEach(joiner::add);

        //自定义错误消息
        getLazyErrorMsgList().forEach(joiner::add);

        //存在错误信息时抛出异常
        String allErrorMsg = joiner.toString();
        if (StringUtils.isNotBlank(allErrorMsg)) {
            throw new ParamException(allErrorMsg);
        }
    }
}
