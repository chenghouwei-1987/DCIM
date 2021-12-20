package com.cmiot.gateway.config.impl;

import com.cmiot.gateway.config.ConfigConsts;
import com.cmiot.gateway.config.IDeviceConfig;
import com.cmiot.gateway.entity.DeviceEntity;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.RUNTIME;

/**
 * 从配置文件中读取设备的相关参数，{@link IDeviceConfig}的默认实现
 * <p>
 * 对应配置文件默认位置为resource/devices.conf
 * 如不使用本实现，请删除此类或删除@Component注解
 */
@Component
@Slf4j
public final class FileDeviceConfig implements IDeviceConfig {

    private final ILogger logger;
    private Config config;

    private ConcurrentMap<String, DeviceEntity> configCache = new ConcurrentHashMap<>(500000);

    public FileDeviceConfig(ILogger logger) {
        this.logger = logger;
    }

    @PostConstruct
    public void init() {
        config = ConfigFactory.load("config/devices.conf");
        config.checkValid(ConfigFactory.defaultReference());
    }

    @Override
    public DeviceEntity getDeviceEntity(String originalIdentity) {

        if (StringUtils.isEmpty(originalIdentity) || !config.hasPath(originalIdentity)) {
            logger.logInnerWarn(log, RUNTIME, "cannot find device entity, originalIdentity=" + originalIdentity);
            return null;
        }

        DeviceEntity deviceEntity = configCache.get(originalIdentity);
        if (deviceEntity != null) {
            return deviceEntity;
        }

        Config deviceConfig = config.getConfig(originalIdentity);
        Long productId = FileConfigUtil.getLongIfExists(deviceConfig, ConfigConsts.PRODUCT_ID);
        String deviceName = FileConfigUtil.getStringIfExists(deviceConfig, ConfigConsts.DEVICE_NAME);
        String deviceId = FileConfigUtil.getStringIfExists(deviceConfig, ConfigConsts.DEVICE_ID);
        String deviceKey = FileConfigUtil.getStringIfExists(deviceConfig, ConfigConsts.DEVICE_KEY);

        if (StringUtils.isNotEmpty(deviceKey) && productId != null && deviceId != null) {
            deviceEntity = DeviceEntity.builder()
                    .productId(productId)
                    .deviceName(deviceName)
                    .deviceId(deviceId)
                    .deviceKey(deviceKey)
                    .build();
            configCache.put(originalIdentity, deviceEntity);
        } else {
            logger.logInnerWarn(log, RUNTIME, "illegal device config, productId deviceId and deviceName must be present");
        }

        return deviceEntity;
    }
}
