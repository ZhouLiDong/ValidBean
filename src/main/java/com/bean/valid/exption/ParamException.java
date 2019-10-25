package com.bean.valid.exption;

/**
 * 参数异常，参数校验异常
 * @Author 周利东
 * @Date: 2019/8/6 9:40
 */
public class ParamException extends CustomRuntimeException {

    public ParamException(){
        this("参数为空");
    }

    public ParamException(String message){
        super(message);
    }

}
