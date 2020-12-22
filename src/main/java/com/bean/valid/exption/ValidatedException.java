package com.bean.valid.exption;

/**
 * @Author 周利东
 * @Date: 2019/9/5 11:33
 */
public class ValidatedException extends CustomRuntimeException {
    public ValidatedException() {
        super("校验失败");
    }

    public ValidatedException(String msg) {
        super(msg);
    }

    public ValidatedException(String msg, Throwable t) {
        super(msg, t);
    }
}
