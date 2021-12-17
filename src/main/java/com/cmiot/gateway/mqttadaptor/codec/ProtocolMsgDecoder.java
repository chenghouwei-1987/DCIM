package com.cmiot.gateway.mqttadaptor.codec;

import com.cmiot.gateway.entity.GatewayMessage;
import com.cmiot.gateway.entity.DeviceMessage;
import io.netty.handler.codec.mqtt.MqttMessage;


/**
 * 协议网关服务内部数据解码，将平台下发的Mqtt消息解码为协议网关服务内部数据
 */
public class ProtocolMsgDecoder {

    /**
     * 内部数据解码
     *
     * @param mqttMessage mqttMessage
     * @return deviceMessage
     */
    public static DeviceMessage decodeDeviceMsg(MqttMessage mqttMessage) {
        return ProtocolMessageUtil.createDeviceMessage(mqttMessage);
    }

    public static GatewayMessage decodeGatewayMsg(MqttMessage mqttMessage) {
        return ProtocolMessageUtil.createGatewayMessage(mqttMessage);
    }
}
