package com.cmiot.gateway.security;

/**
 * 安全策略接口
 */
public interface IAuthorizatorPolicy {

    /**
     * @param deviceId 设备Id
     * @return 是否可写
     */
    boolean canWrite(String deviceId);

    /**
     * @param deviceId 设备Id
     * @return 是否可读
     */
    boolean canRead(String deviceId);

}
