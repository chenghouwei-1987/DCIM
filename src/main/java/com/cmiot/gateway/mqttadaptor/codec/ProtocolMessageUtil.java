package com.cmiot.gateway.mqttadaptor.codec;

import com.cmiot.gateway.entity.*;
import com.cmiot.gateway.exceptions.InternalException;
import com.cmiot.gateway.exceptions.InvalidMqttTopicException;
import com.cmiot.gateway.exceptions.UnknownMessageTypeException;
import com.cmiot.gateway.exceptions.UnsupportedMqttMessageTypeException;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.utils.ExtensionUtils;
import com.cmiot.gateway.utils.SasTokenGenerator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * 协议网关内部数据工具类
 */
public final class ProtocolMessageUtil {
    private ILogger logger = ExtensionUtils.getLogger();


    /**
     * MqttMessage到gateway内部服务消息
     *
     * @param mqttMessage 从平台收到的Mqtt消息
     * @return gateway内部服务消息
     */
    public static GatewayMessage createGatewayMessage(MqttMessage mqttMessage) {

        if (MqttMessageType.PUBLISH != mqttMessage.fixedHeader().messageType()) {
            throw new UnsupportedMqttMessageTypeException("only publish message could be decoded to ProtocolMessage");
        }
        MqttPublishMessage publishMessage = (MqttPublishMessage) mqttMessage;
        String[] tokens = TopicUtils.splitTopic(publishMessage.variableHeader().topicName());
        int minTopicLevel = 4;
        if (tokens.length < minTopicLevel) {
            throw new InvalidMqttTopicException("downlink topic level less than 4");
        }

        String event = tokens[3];
        switch (event) {
            case TopicUtils.CMD:
                //topic形如"$gw-ctrl/${svcId}/${instName}/cmd/response(request)/${cmdId}"
                return createGatewayMessageFromCmdType(tokens, publishMessage);
            default:
                throw new UnknownMessageTypeException("unknown down link message type:" + event);
        }
    }


    @Data
    @AllArgsConstructor
    private static class DeviceInfo {
        long productId;
        String deviceName;
    }

    /**
     * 创建MQTT登陆消息
     *
     * @param deviceMessage protocolMessage
     * @return MQTT登陆消息
     */
    public static MqttMessage createMqttLoginMsg(DeviceMessage deviceMessage) {
        long productId = deviceMessage.getProductId();
        String deviceId = deviceMessage.getDeviceId();
        String deviceName = deviceMessage.getDeviceName();
        String deviceKey = deviceMessage.getDeviceKey();
        String topic = TopicUtils.createLoginTopic(productId, deviceMessage.getDeviceName());
        String sasToken = SasTokenGenerator.deviceSasToken(productId, deviceName, deviceKey);
        if (sasToken == null) {
            throw new InternalException("gen device token failed, deviceId=" + deviceId);
        }
        return createPublishMessage(topic, genDeviceOnlineJsonPayload(sasToken).getBytes());
    }

    /**
     * 创建MQTT登陆消息
     *
     * @param deviceMessage protocolMessage
     * @return MQTT登陆消息
     */
    public static MqttMessage createMqttLogoutMsg(DeviceMessage deviceMessage) {
        String topic = TopicUtils.createLogoutTopic(deviceMessage.getProductId(), deviceMessage.getDeviceName());
        return createPublishMessage(topic, deviceMessage.getData());
    }

    /**
     * 创建MQTT登出消息
     *
     * @param deviceMessage protocolMessage
     * @return MQTT登出消息
     */
    public static MqttMessage createMqttDpMsg(DeviceMessage deviceMessage) {
        String topic = TopicUtils.createDpTopic(deviceMessage.getProductId(), deviceMessage.getDeviceName());
        return createPublishMessage(topic, deviceMessage.getData());
    }

    /**
     * 创建MQTT命令回复消息
     *
     * @param deviceMessage protocolMessage
     * @return MQTT命令回复消息
     */
    public static MqttMessage createMqttCmdReplyMsg(DeviceMessage deviceMessage) {
        if (!(deviceMessage instanceof DeviceCmdMessage)) {
            throw new UnknownMessageTypeException("cmd type message require ProtocolCmdMessage");
        }
        DeviceCmdMessage cmdMessage = (DeviceCmdMessage) deviceMessage;
        String topic = TopicUtils.createCmdRespondTopic(cmdMessage.getProductId(), cmdMessage.getDeviceName(), cmdMessage.getCmdId());
        return createPublishMessage(topic, deviceMessage.getData());
    }

    /**
     * 创建MQTT网关命令回复消息
     *
     * @param gatewayCmdMessage gatewayCmdMessage
     * @param data 回复的消息
     * @return MQTT网关命令回复消息
     */
    public static MqttMessage createMqttGwCmdReplyMsg(GatewayCmdMessage gatewayCmdMessage, byte[] data) {
        String topic = TopicUtils.createGwCmdRespondTopic(gatewayCmdMessage.getSvcId(), gatewayCmdMessage.getInstName(), gatewayCmdMessage.getCmdId());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建MQTT设备镜像更新消息
     *
     * @param deviceMessage protocolMessage
     * @return MQTT设备镜像更新消息
     */
    public static MqttMessage createMqttImageUpdateMsg(DeviceMessage deviceMessage) {
        String topic = TopicUtils.createImageUpdateTopic(deviceMessage.getProductId(), deviceMessage.getDeviceName());
        return createPublishMessage(topic, deviceMessage.getData());
    }

    /**
     * 创建MQTT设备镜像查询消息
     *
     * @param deviceMessage protocolMessage
     * @return MQTT设备镜像查询消息
     */
    public static MqttMessage createMqttImageGetMsg(DeviceMessage deviceMessage) {
        String topic = TopicUtils.createImageGetTopic(deviceMessage.getProductId(), deviceMessage.getDeviceName());
        return createPublishMessage(topic, deviceMessage.getData());
    }

    /**
     * MqttMessage到gateway设备数据的转换
     *
     * @param mqttMessage 从平台收到的Mqtt消息
     * @return gateway 设备消息
     */
    static DeviceMessage createDeviceMessage(MqttMessage mqttMessage) {
        if (MqttMessageType.PUBLISH != mqttMessage.fixedHeader().messageType()) {
            throw new UnsupportedMqttMessageTypeException("only publish message could be decoded to DeviceMessage");
        }
        MqttPublishMessage publishMessage = (MqttPublishMessage) mqttMessage;
        String[] tokens = TopicUtils.splitTopic(publishMessage.variableHeader().topicName());
        int minTopicLevel = 4;
        if (tokens.length < minTopicLevel) {
            throw new InvalidMqttTopicException("downlink topic level less than 4");
        }

        String event = tokens[3];
        switch (event) {
            case TopicUtils.LOGIN:
                return createDeviceMessageFromLoginType(tokens, publishMessage);
            case TopicUtils.LOGOUT:
                return createDeviceMessageFromLogoutType(tokens, publishMessage);
            case TopicUtils.DP:
                return createDeviceMessageFromDpType(tokens, publishMessage);
            case TopicUtils.CMD:
                return createDeviceMessageFromCmdType(tokens, publishMessage);
            case TopicUtils.IMAGE:
                return createDeviceMessageFromImageType(tokens, publishMessage);
            default:
                throw new UnknownMessageTypeException("unknown down link message type:" + event);
        }
    }

    /**
     * 下行login响应到gateway代理连接内部消息的转换
     *
     * @param tokens         分割后的topic tokens
     * @param publishMessage 下行publish消息
     * @return gateway代理连接内部消息
     */
    private static DeviceMessage createDeviceMessageFromLoginType(String[] tokens, MqttPublishMessage publishMessage) {
        if (!TopicUtils.validateDownLinkLoginTopic(tokens)) {
            throw new InvalidMqttTopicException("invalid downlink login topic");
        }
        DeviceInfo deviceInfo = extractDeviceInfoFromTopic(tokens);
        MessageType type = TopicUtils.getDownLinkLoginMessageType(tokens);

        byte[] data = new byte[publishMessage.payload().readableBytes()];
        publishMessage.payload().readBytes(data);

        return DeviceMessage.builder()
                .productId(deviceInfo.getProductId())
                .deviceName(deviceInfo.getDeviceName())
                .messageType(type)
                .data(data)
                .build();
    }

    /**
     * 下行logout响应或notify到gateway代理连接内部消息的转换
     *
     * @param tokens         分割后的topic tokens
     * @param publishMessage 下行publish消息
     * @return gateway代理连接内部消息
     */
    private static DeviceMessage createDeviceMessageFromLogoutType(String[] tokens, MqttPublishMessage publishMessage) {
        if (!TopicUtils.validateDownLinkLogoutTopic(tokens)) {
            throw new InvalidMqttTopicException("invalid downlink logout topic");
        }
        DeviceInfo deviceInfo = extractDeviceInfoFromTopic(tokens);
        MessageType type = TopicUtils.getDownLinkLogoutMessageType(tokens);

        byte[] data = new byte[publishMessage.payload().readableBytes()];
        publishMessage.payload().readBytes(data);

        return DeviceMessage.builder()
                .productId(deviceInfo.getProductId())
                .deviceName(deviceInfo.getDeviceName())
                .messageType(type)
                .data(data)
                .build();
    }

    /**
     * 下行dp响应到gateway代理连接内部消息的转换
     *
     * @param tokens         分割后的topic tokens
     * @param publishMessage 下行publish消息
     * @return gateway代理连接内部消息
     */
    private static DeviceMessage createDeviceMessageFromDpType(String[] tokens, MqttPublishMessage publishMessage) {
        if (!TopicUtils.validateDownLinkDpTopic(tokens)) {
            throw new InvalidMqttTopicException("invalid downlink dp topic");
        }
        DeviceInfo deviceInfo = extractDeviceInfoFromTopic(tokens);
        MessageType type = TopicUtils.getDownLinkDpMessageType(tokens);

        byte[] data = new byte[publishMessage.payload().readableBytes()];
        publishMessage.payload().readBytes(data);

        return DeviceMessage.builder()
                .productId(deviceInfo.getProductId())
                .deviceName(deviceInfo.getDeviceName())
                .messageType(type)
                .data(data)
                .build();
    }

    /**
     * 下行cmd或设备回复后的平台响应到gateway代理连接内部消息的转换
     *
     * @param tokens         分割后的topic tokens
     * @param publishMessage 下行publish消息
     * @return gateway代理连接内部消息
     */
    private static DeviceMessage createDeviceMessageFromCmdType(String[] tokens, MqttPublishMessage publishMessage) {
        if (!TopicUtils.validateDownLinkCmdTopic(tokens)) {
            throw new InvalidMqttTopicException("invalid downlink cmd topic");
        }
        DeviceInfo deviceInfo = extractDeviceInfoFromTopic(tokens);
        MessageType type = TopicUtils.getDownLinkCmdMessageType(tokens);

        byte[] data = new byte[publishMessage.payload().readableBytes()];
        publishMessage.payload().readBytes(data);

        return new DeviceCmdMessage(deviceInfo.getProductId(), deviceInfo.getDeviceName(), type, data, tokens[5]);
    }

    private static GatewayMessage createGatewayMessageFromCmdType(String[] tokens, MqttPublishMessage publishMessage) {
        if (!TopicUtils.validateGwCmdTopic(tokens)) {
            throw new InvalidMqttTopicException("invalid gateway cmd topic");
        }
        MessageType type = TopicUtils.getGwCmdMessageType(tokens);
        byte[] data = new byte[publishMessage.payload().readableBytes()];
        publishMessage.payload().readBytes(data);
        return new GatewayCmdMessage(tokens[5], tokens[1], tokens[2], type, data);
    }

    /**
     * 下行image update、get响应或delta消息到gateway代理连接内部消息的转换
     *
     * @param tokens         分割后的topic tokens
     * @param publishMessage 下行publish消息
     * @return gateway代理连接内部消息
     */
    private static DeviceMessage createDeviceMessageFromImageType(String[] tokens, MqttPublishMessage publishMessage) {
        if (!TopicUtils.validateDownLinkImageTopic(tokens)) {
            throw new InvalidMqttTopicException("invalid downlink image topic");
        }

        DeviceInfo deviceInfo = extractDeviceInfoFromTopic(tokens);
        MessageType type = TopicUtils.getDownLinkImageMessageType(tokens);

        byte[] data = new byte[publishMessage.payload().readableBytes()];
        publishMessage.payload().readBytes(data);

        return DeviceMessage.builder()
                .productId(deviceInfo.getProductId())
                .deviceName(deviceInfo.getDeviceName())
                .messageType(type)
                .data(data)
                .build();
    }

    /**
     * @param tokens 分割后的topic tokens
     * @return 设备信息
     */
    private static DeviceInfo extractDeviceInfoFromTopic(String[] tokens) {
        long pid;
        String deviceName = tokens[2];
        try {
            pid = Long.parseLong(tokens[1]);
        } catch (NumberFormatException e) {
            //should never be here
            throw new InvalidMqttTopicException("invalid pid format in downlink topic");
        }
        return new DeviceInfo(pid, deviceName);
    }

    /**
     * @param topic   上行publish消息的mqtt topic
     * @param payload 上行publish消息的payload
     * @return MqttMessage
     */
    private static MqttMessage createPublishMessage(String topic, byte[] payload) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(topic, 0);
        payload = payload == null ? new byte[0] : payload;
        ByteBuf byteBuf = Unpooled.wrappedBuffer(payload);
        return new MqttPublishMessage(fixedHeader, variableHeader, byteBuf);
    }

    /**
     * 生成设备登录用的Payload
     *
     * @param sasToken sasToken
     * @return 设备登录用的Payload
     */
    private static String genDeviceOnlineJsonPayload(String sasToken) {
        return "{\"Authorization\": \""
                + sasToken
                + "\"}";
    }
}
