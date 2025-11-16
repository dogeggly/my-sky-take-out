package com.sky.exception;

/**
 * 账号被锁定异常
 */
public class AccountException extends BaseException {

    public AccountException(String msg) {
        super(msg);
    }

}
