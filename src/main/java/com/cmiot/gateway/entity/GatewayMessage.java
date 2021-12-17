package com.cmiot.gateway.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * 协议网关内部服务数据实体类
 */
@AllArgsConstructor
@Getter
public class GatewayMessage {
    /**
     * 消息类型，不可为空
     */
    @NonNull
    private MessageType messageType;

    /**
     * 数据内容，可为空
     */
    private byte[] data;

    private String svcId;

    private String instName;

}
