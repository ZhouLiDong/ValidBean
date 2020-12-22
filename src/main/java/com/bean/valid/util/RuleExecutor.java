package com.bean.valid.util;


import java.util.function.Function;

/**
 * @Author: 周利东
 * @Date: 2020/12/11 14:14
 * @Description: 规则执行器
 */
public class RuleExecutor<E, FT> {
    private RuleConfig<E, FT> ruleConfig;
    private String errorMsg = "";

    private RuleExecutor(RuleConfig<E, FT> ruleConfig) {
        this.ruleConfig = ruleConfig;
    }


    /**
     * 创建规则执行对象
     *
     * @param ruleConfig
     * @param <E>
     * @param <FT>
     * @return
     */
    public static <E, FT> RuleExecutor<E, FT> create(RuleConfig<E, FT> ruleConfig) {
        return new RuleExecutor<>(ruleConfig);
    }

    /**
     * 执行规则
     *
     * @return
     */
    public boolean execRule() {
        //获取要被校验的值
        FT fieldValue = ruleConfig.getFieldValue();
        //执行校验函数校验
        Function<FT, Boolean> ruleFunc = ruleConfig.getRuleFunc();
        Boolean success = ruleFunc.apply(fieldValue);
        if (Boolean.FALSE.equals(success)) {
            errorMsg = ruleConfig.getErrorMsg();
        }

        if (success == null) {
            errorMsg = "规则函数为空！";
            success = Boolean.FALSE;
        }

        return success;
    }

    /**
     * 获取错误信息
     *
     * @return
     */
    public String getErrorMsg() {
        return this.errorMsg;
    }

}
