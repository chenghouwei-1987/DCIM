package com.cmiot.gateway.entity;

import com.cmiot.gateway.mqttadaptor.MqttClient;
import io.netty.channel.Channel;
import javafx.util.Pair;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 代理连接Session
 * <p>
 * 一个代理连接代理多个设备连接，负责这些设备与平台之间的通信
 */
@Data
@Builder
public final class ProxySession {

    /**
     * 此代理连接代理的DeviceInfo和DeviceSession的映射关系
     * 其中Pair<Long, String>中Long为产品ID，String为设备名称
     */
    private ConcurrentMap<Pair<Long, String>, DeviceSession> proxyDevAssociation = new ConcurrentHashMap<>();

    /**
     * MQTT Client
     */
    private MqttClient mqttClient;

    /**
     * 代理连接ID
     */
    private String proxyId;

    /**
     * 代理连接绑定的Netty Channel
     */
    private Channel channel;

    /**
     * 代理连接是否连接
     */
    private volatile boolean connected;

    /**
     * 代理的设备数量是否达到限制
     */
    private volatile boolean isDevicesReachedLimit;

    /**
     * 保存此代理连接代理的设备Session
     *
     * @param deviceSession 设备Session
     */
    public void putDevSession(DeviceSession deviceSession) {
        proxyDevAssociation.put(new Pair<>(deviceSession.getProductId(), deviceSession.getDeviceName()), deviceSession);
    }

    /**
     * 获取此代理连接代理的设备Session
     *
     * @param productId  产品ID
     * @param deviceName 设备名称
     * @return 设备Session
     */
    public DeviceSession getDevSession(long productId, String deviceName) {
        return proxyDevAssociation.get(new Pair<>(productId, deviceName));
    }

    /**
     * 移出此代理连接代理的设备Session
     *
     * @param productId  产品ID
     * @param deviceName 设备名称
     */
    public void removeDevSession(long productId, String deviceName) {
        proxyDevAssociation.remove(new Pair<>(productId, deviceName));
    }

    /**
     * 获取此代理连接代理的设备数量
     *
     * @return 代理的设备数量
     */
    public int size() {
        return proxyDevAssociation.size();
    }
}
