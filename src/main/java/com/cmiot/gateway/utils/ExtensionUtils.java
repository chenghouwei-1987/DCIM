package com.cmiot.gateway.utils;

import com.cmiot.gateway.config.IDeviceConfig;
import com.cmiot.gateway.config.IGatewayConfig;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.extensions.metric.Metric;
import com.cmiot.gateway.security.IAuthenticator;
import com.cmiot.gateway.security.IAuthorizatorPolicy;
import lombok.extern.slf4j.Slf4j;

/**
 * 扩展工具类，使用 {@link SpringUtil} 获取 Spring Bean
 */
@Slf4j
public class ExtensionUtils {

    private ExtensionUtils() {
    }

    /**
     * 获取ILogger扩展工具类
     *
     * @return ILogger扩展工具类
     */
    public static ILogger getLogger() {
        try {
            return SpringUtil.getBean(ILogger.class);
        } catch (Exception e) {
            log.error("cannot init ILogger, please ensure there are only one implement of ILogger interface with @Component annotation");
            System.exit(1);
        }
        return null;
    }

    /**
     * 获取IGatewayConfig扩展工具类
     *
     * @return IGatewayConfig扩展工具类
     */
    public static IGatewayConfig getGatewayConfig() {
        try {
            return SpringUtil.getBean(IGatewayConfig.class);
        } catch (Exception e) {
            log.error("cannot init ILogger, please ensure there are only one implement of ILogger interface with @Component annotation");
            System.exit(1);
        }
        return null;
    }

    /**
     * 获取IDeviceConfig扩展工具类
     *
     * @return IDeviceConfig扩展工具类
     */
    public static IDeviceConfig getDeviceConfig() {
        try {
            return SpringUtil.getBean(IDeviceConfig.class);
        } catch (Exception e) {
            log.error("cannot init ILogger, please ensure there are only one implement of ILogger interface with @Component annotation");
            System.exit(1);
        }
        return null;
    }

    /**
     * 获取Metric bean
     *
     * @return Metric bean
     */
    public static Metric getMetric() {
        try {
            return SpringUtil.getBean(Metric.class);
        } catch (Exception e) {
            log.error("cannot init Metric", e);
            System.exit(1);
        }
        return null;
    }

    /**
     * 获取IAuthenticator扩展工具类
     *
     * @return IAuthenticator扩展工具类
     */
    public static IAuthenticator getAuthenticator() {
        try {
            return SpringUtil.getBean(IAuthenticator.class);
        } catch (Exception e) {
            log.error("cannot init IAuthenticator, please ensure there are only one implement of IAuthenticator interface with @Component annotation");
            System.exit(1);
        }
        return null;
    }

    /**
     * 获取IAuthorizatorPolicy扩展工具类
     *
     * @return IAuthorizatorPolicy扩展工具类
     */
    public static IAuthorizatorPolicy getAuthorizatorPolicy() {
        try {
            return SpringUtil.getBean(IAuthorizatorPolicy.class);
        } catch (Exception e) {
            log.error("cannot init IAuthorizatorPolicy, please ensure there are only one implement of IAuthorizatorPolicy interface with @Component annotation");
            System.exit(1);
        }
        return null;
    }
}
