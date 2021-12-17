package com.cmiot.gateway.config.impl;

import com.cmiot.gateway.config.ConfigConsts;
import com.cmiot.gateway.config.IGatewayConfig;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 从配置文件中读取协议网关服务的相关参数，{@link IGatewayConfig}的默认实现
 * <p>
 * 对应配置文件默认位置为resource/gateway.conf
 * 如不使用本实现，请删除此类或删除@Component注解
 */
@Component
public final class FileGatewayConfig implements IGatewayConfig {

    private final ILogger logger;

    private Config config;

    private ConcurrentMap<String, Object> configCache = new ConcurrentHashMap<>(100);

    public FileGatewayConfig(ILogger logger) {
        this.logger = logger;
    }

    @PostConstruct
    public void init() {
        config = ConfigFactory.load("config/gateway.conf");
        config.checkValid(ConfigFactory.defaultReference());
    }

    @Override
    public String getConnectionHost() {
        return getString(ConfigConsts.CONNECTION_HOST);
    }

    @Override
    public String getServiceId() {
        return getString(ConfigConsts.GATEWAY_SERVICE_ID);
    }

    @Override
    public String getServiceKey() {
        return getString(ConfigConsts.GATEWAY_SERVICE_KEY);
    }

    @Override
    public String getInstanceName() {
        return getString(ConfigConsts.GATEWAY_INSTANCE_NAME);
    }

    @Override
    public String getInstanceKey() {
        return getString(ConfigConsts.GATEWAY_INSTANCE_KEY);
    }

    @Override
    public String getServiceName() {
        return getString(ConfigConsts.GATEWAY_SERVICE_NAME);
    }

    @Override
    public List<String> getTransport() {
        String transport = getString(ConfigConsts.GATEWAY_TRANSPORTS);
        return transport == null || transport.trim().isEmpty() ? null : Arrays.stream(transport
                .split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean tlsSupport() {
        return getBoolean(ConfigConsts.GATEWAY_TLS_SUPPORT);
    }


    private String getString(String name) {
        if (configCache.containsKey(name)) {
            return (String) configCache.get(name);
        }

        String value = FileConfigUtil.getStringIfExists(config, name);
        if (StringUtils.isNotEmpty(value)) {
            configCache.put(name, value);
        }
        return value;
    }

    private Integer getInteger(String name) {
        if (configCache.containsKey(name)) {
            return (Integer) configCache.get(name);
        }
        Integer value = FileConfigUtil.getIntegerIfExists(config, name);
        if (value != null) {
            configCache.put(name, value);
        }
        return value;
    }

    private Boolean getBoolean(String name) {
        if (configCache.containsKey(name)) {
            return (Boolean) configCache.get(name);
        }
        Boolean value = FileConfigUtil.getBooleanIfExists(config, name);
        if (value != null) {
            configCache.put(name, value);
        }
        return value;
    }


}
