package com.test.exception;

/**
 * Created by Administrator on 2017/6/29.
 * 秒杀关闭
 */
public class SeckillCloseException extends SeckillException {
    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
