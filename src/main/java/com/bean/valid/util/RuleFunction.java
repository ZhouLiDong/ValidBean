package com.bean.valid.util;

import java.util.function.Function;

/**
 * 校验函数
 * @Author 周利东
 * @Date: 2020/12/11 16:01
 *
 * @param <P>
 */
@FunctionalInterface
public interface RuleFunction<P> extends Function<P, Boolean> {

    /**
     * @param valid 被校验的值
     * @return true:校验通过。false:校验未通过
     */
    @Override
    Boolean apply(P valid);
}