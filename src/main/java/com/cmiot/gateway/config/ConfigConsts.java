package com.cmiot.gateway.config;

/**
 * 配置文件路径常量类
 */
public final class ConfigConsts {

    public static final String DEFAULT_CONNECTION_HOST = "tcp://183.230.40.96:1883";

    public static final String DEFAULT_TLS_CONNECTION_HOST = "tcp://183.230.40.16:8883";

    public static final String DEFAULT_TRANSPORT = "tcp:10086";

    public static final Boolean DEFAULT_TLS_SUPPORT = Boolean.FALSE;

    public static final String CONNECTION_HOST = "connectionHost";

    public static final String GATEWAY_SERVICE_ID = "serviceId";

    public static final String GATEWAY_SERVICE_NAME = "serviceName";

    public static final String GATEWAY_SERVICE_KEY = "serviceKey";

    public static final String GATEWAY_INSTANCE_NAME = "instanceName";

    public static final String GATEWAY_INSTANCE_KEY = "instanceKey";

    public static final String GATEWAY_TRANSPORTS = "transports";

    public static final String GATEWAY_TLS_SUPPORT = "tlsSupport";

    public static final String PRODUCT_ID = "productId";

    public static final String DEVICE_NAME = "deviceName";

    public static final String DEVICE_ID = "deviceId";

    public static final String DEVICE_KEY = "deviceKey";

    public static final long MAX_PROXY_CONNECTION_CREATION_INTERVAL_MS = 2000;

}
