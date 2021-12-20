package com.cmiot.gateway.exceptions;

/**
 * 未知消息类型异常
 */
public final class UnknownMessageTypeException extends RuntimeException{
    public UnknownMessageTypeException(Throwable cause) {
        super(cause);
    }

    public UnknownMessageTypeException(String message) {
        super(message);
    }

    public UnknownMessageTypeException(String message,Throwable cause) {
        super(message,cause);
    }
}
