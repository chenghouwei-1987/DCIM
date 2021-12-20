package com.cmiot.gateway.security;

import org.springframework.stereotype.Component;

/**
 * {@link IAuthenticator}的默认实现
 */
@Component
public class DefaultAuthenticator implements IAuthenticator {

    @Override
    public boolean checkValid(String deviceId) {
        return true;
    }

}
