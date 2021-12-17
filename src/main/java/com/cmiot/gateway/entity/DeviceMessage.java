package com.cmiot.gateway.entity;

import lombok.*;

/**
 * 协议网关内部数据实体类，用于自定义协议数据与OneNET MQTT接入机之间转换的中间数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceMessage {
    /**
     * 产品ID，不可为空
     */
    @NonNull
    private long productId;

    /**
     * 设备ID，登录时不可为空
     */
    private String deviceId;

    /**
     * 设备名称，不可为空
     */
    @NonNull
    private String deviceName;

    /**
     * 设备Key，登录时不可为空
     */
    private String deviceKey;

    /**
     * 消息类型，不可为空
     */
    @NonNull
    private MessageType messageType;

    /**
     * 设备数据，可为空
     */
    private byte[] data;
}
