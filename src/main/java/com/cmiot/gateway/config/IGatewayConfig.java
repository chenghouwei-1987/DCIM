package com.cmiot.gateway.config;

import java.util.List;

/**
 * 设备配置接口类，必须实现
 */
public interface IGatewayConfig {

    /**
     * OneNET接入机连接地址，此方法可返回null或空值
     *
     * @return connection host OneNET接入机连接地址
     */
    String getConnectionHost();

    /**
     * 协议网关服务ID，此方法不可返回null或空值
     *
     * @return 协议网关服务ID
     */
    String getServiceId();

    /**
     * 协议网关服务名称，此方法不可返回null或空值
     *
     * @return 协议网关服务名称
     */
    String getServiceName();

    /**
     * 协议网关服务Key，此方法可返回null或空值
     *
     * @return 协议网关服务Key
     */
    String getServiceKey();

    /**
     * 协议网关服务实例ID，此方法不可返回null或空值
     *
     * @return 协议网关服务实例ID
     */
    String getInstanceName();

    /**
     * 协议网关服务实例Key，此方法不可返回null或空值
     *
     * @return 协议网关服务实例Key
     */
    String getInstanceKey();

    /**
     * 协议网关服务协议站启动的协议列表，每个协议的格式为 Transport:Port
     * 此方法可返回null或空值，默认为TCP:1883
     *
     * @return 协议网关服务协议站启动的协议列表
     */
    List<String> getTransport();

    /**
     * 是否支持TLS加密，此方法可返回null或空值，默认为true
     *
     * @return 是否支持TLS加密
     */
    Boolean tlsSupport();

}

