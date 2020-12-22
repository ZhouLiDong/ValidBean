package com.bean.valid.util;

import com.bean.valid.exption.ParamException;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 通过Bean的map方法，创建本类对PT类型的对象的指定方法引用返回的对象实例(ST类型)进行校验
 *
 * @param <ST> 被校验的子类型
 * @param <PT> 被校验的父类型
 */
public class FieldValid<ST, PT> extends AbstractEntityValid<ST> {
    private AbstractEntityValid<PT> parentBean;

    FieldValid(AbstractEntityValid<PT> parentBean, ST paramBean, boolean lazyEnable) {
        super(paramBean, lazyEnable);

        if (parentBean == null) {
            throw new ParamException("父校验bean不能为空");
        }
        this.parentBean = parentBean;

    }

    public AbstractEntityValid<PT> getParentBean() {
        return parentBean;
    }

    public void setParentBean(AbstractEntityValid<PT> parentBean) {
        this.parentBean = parentBean;
    }

    /**
     * 跳转到父属性校验
     *
     * @return
     */
    public EntityValid<PT> parent() {
        return new EntityValid<PT>((PT) getParentBean().getEntity(), isLazy());
    }

    /**
     * 获取祖先链的ValidTriple列表
     *
     * @return
     */
    public List<RuleConfig<? extends Object,? extends Object>> getValidTripleListForAncestorChain() {
        List<RuleConfig<? extends Object,? extends Object>> list = new ArrayList<>();

        AbstractEntityValid<PT> parentBean = getParentBean();

        //当前的祖先链=父的链 + 父的祖先链

        //祖先链
        if (parentBean instanceof FieldValid) {
            list.addAll(((FieldValid) parentBean).getValidTripleListForAncestorChain());
        }

        //父
        list.addAll(parentBean.getRuleConfigList());

        return list;
    }

    @Override
    public void complete() {
        //执行校验并添加错误信息

        //执行父链校验函数
        if (parentBean.isLazy()) {
            getValidTripleListForAncestorChain().forEach(this::validValueAndAddErrorMsg);
        }

        //执行当前Bean校验函数
        if (isLazy()) {
            getRuleConfigList().forEach(this::validValueAndAddErrorMsg);
        }

        if (parentBean.isLazy() || isLazy()) {

            //存在错误信息时抛出异常
            throwExceptionIfExitsErrorMsg();
        }
    }

    /**
     * Bean执行链中是否存在一个及其以上使用默认提示信息的
     *
     * @return
     */
    @Override
    protected boolean getUseDefaultErrorMsgEnableForChain() {
        //父与子是否使用了默认错误提示信息
        return parentBean.getUseDefaultErrorMsgEnableForChain() || getUseDefaultErrorMsgEnable();
    }

    /**
     * 获取所有默认的错误提示信息
     *
     * @return
     */
    private Set<String> getAllDefaultErrorMsg() {
        Set<String> set = new LinkedHashSet<>();

        //父Bean调用链中是否存在使用默认错误提示信息的
        boolean defaultErrorMsgEnableForParentChain = parentBean.getUseDefaultErrorMsgEnableForChain();

        //当前Bean是否存在使用默认错误提示信息的
        boolean currentDefaultErrorMsgEnable = getUseDefaultErrorMsgEnable();

        //添加父链和当前默认错误
        if (defaultErrorMsgEnableForParentChain) {

            //添加父链默认错误
            if (parentBean instanceof FieldValid) {
                set.addAll(((FieldValid) parentBean).getAllDefaultErrorMsg());
            } else {
                set.add(parentBean.getDefaultErrorMsg());
            }
        }

        //添加当前默认错误
        if (currentDefaultErrorMsgEnable) {
            set.add(getDefaultErrorMsg());
        }

        return set;
    }


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
