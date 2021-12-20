package com.cmiot.gateway.security;

/**
 * 鉴权接口
 */
public interface IAuthenticator {

    /**
     * 检查是否有效
     *
     * @param deviceId 设备ID
     * @return 是否有效
     */
    boolean checkValid(String deviceId);

}
