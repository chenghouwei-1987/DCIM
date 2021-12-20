package com.cmiot.gateway.exceptions;

/**
 * 设备未登录异常
 */
public final class DeviceNotLoginException extends RuntimeException {
    public DeviceNotLoginException(Throwable cause) {
        super(cause);
    }

    public DeviceNotLoginException(String message) {
        super(message);
    }

    public DeviceNotLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
