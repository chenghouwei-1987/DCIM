package com.cmiot.gateway.config;

import com.cmiot.gateway.entity.DeviceEntity;

/**
 * 设备配置接口类，默认不使用
 */
public interface IDeviceConfig {

    /**
     * 获取OneNET设备相关信息
     *
     * @param originalIdentity 每个设备的唯一识别字符串
     * @return OneNET设备实体相关信息
     */
    DeviceEntity getDeviceEntity(String originalIdentity);
}
