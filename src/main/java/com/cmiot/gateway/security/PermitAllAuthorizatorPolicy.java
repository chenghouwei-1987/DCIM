package com.cmiot.gateway.security;

import org.springframework.stereotype.Component;

/**
 * {@link IAuthorizatorPolicy}的默认实现
 */
@Component
public class PermitAllAuthorizatorPolicy implements IAuthorizatorPolicy {

    @Override
    public boolean canWrite(String deviceId) {
        return true;
    }

    @Override
    public boolean canRead(String deviceId) {
        return true;
    }

}
