package com.cmiot.gateway.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 命令消息实体类，用于与平台进行通信
 */
@Data
@AllArgsConstructor
public final class DeviceCmdMessage extends DeviceMessage {
    private String cmdId;

    private DeviceCmdMessage(long productId, String deviceId, String deviceName, String deviceKey, MessageType messageType, byte[] data, String cmdId) {
        super(productId, deviceId, deviceName, deviceKey, messageType, data);
        this.cmdId = cmdId;
    }

    public DeviceCmdMessage(long productId, String deviceName, MessageType messageType, byte[] data, String cmdId) {
        this(productId, null, deviceName, null, messageType, data, cmdId);
    }

    public static DeviceCmdMessageBuilder builder() {
        return new DeviceCmdMessage.DeviceCmdMessageBuilder();
    }

    public static class DeviceCmdMessageBuilder extends DeviceMessageBuilder {

        private long productId;
        private String deviceId;
        private String deviceName;
        private String deviceKey;
        private MessageType messageType;
        private byte[] data;
        private String cmdId;

        public DeviceCmdMessageBuilder cmdId(String cmdId) {
            this.cmdId = cmdId;
            return this;
        }


        @Override
        public DeviceCmdMessageBuilder productId(long productId) {
            this.productId = productId;
            return this;
        }

        @Override
        public DeviceCmdMessageBuilder deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        @Override
        public DeviceCmdMessageBuilder deviceName(String deviceName) {
            this.deviceName = deviceName;
            return this;
        }

        @Override
        public DeviceCmdMessageBuilder deviceKey(String deviceKey) {
            this.deviceKey = deviceKey;
            return this;
        }

        @Override
        public DeviceCmdMessageBuilder messageType(MessageType messageType) {
            this.messageType = messageType;
            return this;
        }

        @Override
        public DeviceCmdMessageBuilder data(byte[] data) {
            this.data = data;
            return this;
        }

        @Override
        public DeviceCmdMessage build() {
            return new DeviceCmdMessage(this.productId, this.deviceId, this.deviceName, this.deviceKey, this.messageType, this.data, this.cmdId);
        }
    }
}