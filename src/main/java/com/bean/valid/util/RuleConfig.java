package com.bean.valid.util;

import java.util.function.Function;


/**
 *
 */

/**
 * 校验规则配置
 * @Author 周利东
 * @Date: 2020/12/11 11:54
 * @param <E> 被校验实体对象类型
 * @param <FT> 被校验字段类型
 */
public class RuleConfig<E, FT> {

    /**
     * 校验目标实体对象
     */
    E entity;

    /**
     * 获取校验目标实体对象字段函数
     */
    Function<E, FT> fieldFunc;

    /**
     * 校验规则函数
     */
    Function<FT, Boolean> ruleFunc;

    /**
     * validFieldFunc函数返回false时（校验未通过时）的错误信息
     */
    String errorMsg;

    RuleConfig(E entity, Function<E, FT> fieldFunc, Function<FT, Boolean> ruleFunc, String errorMsg) {
        if (entity == null) {

        }

        this.entity = entity;
        this.fieldFunc = fieldFunc;
        this.ruleFunc = ruleFunc;
        this.errorMsg = errorMsg;
    }

    FT getFieldValue() {
        return fieldFunc.apply(entity);
    }

    public Function<FT, Boolean> getRuleFunc() {
        return ruleFunc;
    }


    public String getErrorMsg() {
        return errorMsg;
    }
}
