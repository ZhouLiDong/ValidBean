package com.bean.valid.util;

import com.bean.valid.exption.ParamException;

import java.util.Objects;
import java.util.function.Function;

/**
 * @Author 周利东
 * @Date: 2020/12/11 10:34
 */
public class EntityValid<T> extends AbstractEntityValid<T> {
    public EntityValid(T entity, boolean lazyEnable){
        super(entity,lazyEnable);
    }

    /**
     * 转换为正对这个实体的字段进行校验
     *
     * @param mapper
     * @param <ST>
     * @return
     */
    public <ST> FieldValid<ST, T> map(Function<T, ST> mapper) {
        Objects.requireNonNull(mapper);
        ST subParamBean = mapper.apply(getEntity());
        if (subParamBean == null) {
            throw new ParamException("被校验的子属性不能为空");
        }
        return new FieldValid<>(this, subParamBean, isLazy());
    }

}
