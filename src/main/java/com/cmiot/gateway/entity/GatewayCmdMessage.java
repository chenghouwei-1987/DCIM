package com.cmiot.gateway.entity;

import lombok.Getter;

/**
 * 控制连接接收的命令消息实体类，用于与平台进行通信
 */
@Getter
public class GatewayCmdMessage extends GatewayMessage {
    private String cmdId;

    public GatewayCmdMessage(String cmdId, String svcId, String instName, MessageType messageType, byte[] data) {
        super(messageType, data, svcId, instName);
        this.cmdId = cmdId;
    }
}
