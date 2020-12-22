package com.bean.valid.util;

import com.bean.valid.exption.ValidatedException;

import java.lang.reflect.Constructor;

/**
 * @Author 周利东
 * @Date: 2020/12/11 13:42
 */
abstract class ValidConstant {
    /**
     * 默认错误信息
     */
    static String defaultErrorMsg = "校验失败！";

    /**
     * 默认全局异常构造方法（单个参数，并且是String类型）
     */
    static Constructor<? extends RuntimeException> defaultGlobalExConstructor;

    static {
        //设置默认的全局异常构造函数
        try {
            defaultGlobalExConstructor =  ValidatedException.class.getConstructor(String.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
