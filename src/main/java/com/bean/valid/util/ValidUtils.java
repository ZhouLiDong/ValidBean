package com.bean.valid.util;


/**
 * 实体校验工具
 *
 * @Author 周利东
 * @Date: 2019/9/4 14:40
 */
public interface ValidUtils {

    /**
     * 创建Bean实例，默认延迟校验，调用complete方法时才会执行校验，不符合校验则抛出异常
     *
     * @param bean
     * @return
     */
    static <P> EntityValid<P> ofLazy(P bean) {
        return new EntityValid(bean, true);
    }

    /**
     * 创建Bean实例，默认立即校验，每次调用valid方法都会立即校验，如果不符合校验则立即抛出异常
     *
     * @param bean
     * @return
     */
    static <P> EntityValid<P> of(P bean) {
        return new EntityValid<>(bean, false);
    }


}
