package com.cmiot.gateway.entity;

import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Data;

/**
 * 控制连接Session实体类
 * <p>
 * 与OneNET接入机的控制连接会在协议网关服务实例启动时建立，且每个实例(以服务ID和服务实例名为标识)仅会保持一个控制连接
 */
@Data
@Builder
public final class ControlSession {
    /**
     * 协议网关服务ID
     */
    private String serviceId;
    /**
     * 协议网关服务实例名称
     */
    private String instanceName;
    /**
     * 控制连接对应的Channel
     */
    private Channel channel;
}
