package com.cmiot.gateway.entity;

import com.cmiot.gateway.mqttadaptor.handler.UpLinkChannelHandler;
import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * 设备连接Session实体类，
 * <p>
 * 设备连接Session会在设备连接协议网关服务后建立，设备与设备Session一对一映射
 */
@Data
@Builder
public final class DeviceSession {
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
     * 设备连接对应的Channel
     */
    private Channel channel;

    /**
     * 设备是否已连接，仅指是否连接到协议站和通过安全策略
     * <p>
     * 调用{@link UpLinkChannelHandler#doDeviceOnline(DeviceMessage)} 时设置为true
     * 默认或连接断开时设置为false
     */
    private volatile boolean connected;

    /**
     * 代理此设备的代理连接Session
     */
    private ProxySession proxySession;
}
