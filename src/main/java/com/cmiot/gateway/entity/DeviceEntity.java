package com.cmiot.gateway.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * OneNET设备实体类，对应OneNET平台注册设备
 */
@Data
@Builder
public final class DeviceEntity {

    /**
     * 产品ID
     */
    @NonNull
    private long productId;

    /**
     * 设备名称
     */
    @NonNull
    private String deviceName;

    /**
     * 设备ID
     */
    @NonNull
    private String deviceId;

    /**
     * 设备Key
     */
    @NonNull
    private String deviceKey;
}
