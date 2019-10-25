package com.bean.valid.exption;

/**
 * 自定义异常都继承该类，以便拦截器拦截
 * @Author 周利东
 * @Date: 2019/8/6 9:41
 */
public abstract class CustomRuntimeException extends RuntimeException {
    public CustomRuntimeException(String errorMsg){
        super(errorMsg);
    }
    public CustomRuntimeException(String errorMsg, Throwable throwable){
        super(errorMsg,throwable);
    }
}
