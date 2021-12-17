package com.cmiot.gateway.mqttadaptor.handler;

import com.cmiot.gateway.entity.*;
import com.cmiot.gateway.exceptions.DeviceNotLoginException;
import com.cmiot.gateway.exceptions.UnknownMessageTypeException;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.mqttadaptor.ControlSessionManager;
import com.cmiot.gateway.mqttadaptor.ProxySessionManager;
import com.cmiot.gateway.mqttadaptor.codec.ProtocolMessageUtil;
import com.cmiot.gateway.protocolhub.DevSessionManager;
import com.cmiot.gateway.utils.ExtensionUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.*;

/**
 * 用于上行消息给平台
 */
@Slf4j
public final class UpLinkChannelHandler {

    private ILogger logger = ExtensionUtils.getLogger();

    /**
     * 登录代理设备
     *
     * @param deviceMessage protocolMessage
     */
    public void doDeviceOnline(DeviceMessage deviceMessage) {
        String deviceId = deviceMessage.getDeviceId();
        DeviceSession deviceSession = DevSessionManager.getDevSession(deviceId);
        if (deviceSession == null) {
            logger.logDevWarn(log, LOGIN, deviceMessage.getProductId(), deviceId, "session not existed");
            return;
        }
        deviceSession.setConnected(true);
        ProxySession proxySession = deviceSession.getProxySession();
        if (proxySession == null) {
            proxySession = ProxySessionManager.chooseProxySession();
            if (proxySession == null) {
                logger.logDevWarn(log, LOGIN, deviceMessage.getProductId(), deviceId, "no available proxy session");
                return;
            }
            deviceSession.setProxySession(proxySession);
            proxySession.putDevSession(deviceSession);
        }

        if (null == deviceSession.getProxySession().getChannel() || !deviceSession.getProxySession().getChannel().isActive()) {
            logger.logDevWarn(log, LOGIN, deviceMessage.getProductId(), deviceId, "session existed but channel unavailable");
            return;
        }
        MqttMessage mqttMessage = ProtocolMessageUtil.createMqttLoginMsg(deviceMessage);
        proxySession.getChannel().writeAndFlush(mqttMessage);
    }

    /**
     * 主动登出代理设备
     *
     * @param deviceMessage protocolMessage
     */
    public void doDeviceOffline(DeviceMessage deviceMessage) {
        DeviceSession deviceSession = DevSessionManager.getDevSession(deviceMessage.getDeviceId());
        if (null == deviceSession || !deviceSession.isConnected()) {
            logger.logDevWarn(log, LOGOUT, deviceMessage.getProductId(), deviceMessage.getDeviceId(), "offline request canceled due to device not login");
            return;
        }
        // logout消息带有payload会被平台接入机拒绝
        deviceMessage.setData(null);
        MqttMessage mqttMessage = ProtocolMessageUtil.createMqttLogoutMsg(deviceMessage);
        deviceSession.getProxySession().getChannel().writeAndFlush(mqttMessage);
    }

    /**
     * 发布ProtocolMessage到平台
     *
     * @param deviceMessage protocolMessage
     */
    public void doPublish(DeviceMessage deviceMessage) {
        DeviceSession deviceSession = DevSessionManager.getDevSession(deviceMessage.getDeviceId());
        if (null == deviceSession || !deviceSession.isConnected()) {
            logger.logDevWarn(log, GW_UP_LINK, deviceMessage.getProductId(), deviceMessage.getDeviceId(), "device not login");
            throw new DeviceNotLoginException("device not login");
        }
        MqttMessage mqttMessage;
        switch (deviceMessage.getMessageType()) {
            case DP:
                mqttMessage = ProtocolMessageUtil.createMqttDpMsg(deviceMessage);
                break;
            case CMD_REPLY:
                mqttMessage = ProtocolMessageUtil.createMqttCmdReplyMsg(deviceMessage);
                break;
            case IMAGE_UPDATE:
                mqttMessage = ProtocolMessageUtil.createMqttImageUpdateMsg(deviceMessage);
                break;
            case IMAGE_GET:
                // get image消息带有payload会被平台接入机拒绝
                deviceMessage.setData(null);
                mqttMessage = ProtocolMessageUtil.createMqttImageGetMsg(deviceMessage);
                break;
            default:
                throw new UnknownMessageTypeException("unsupported uplink message type:" + deviceMessage.getMessageType());
        }
        deviceSession.getProxySession().getChannel().writeAndFlush(mqttMessage);
    }

    /**
     * 上行网关命令消息回复到平台
     *
     * @param gatewayCmdMessage 接收到的gatewayCmdMessage
     * @param data            回复的消息
     */
    public void doGwCmdReply(GatewayCmdMessage gatewayCmdMessage, byte[] data) {
        ControlSession session = ControlSessionManager.getSession();
        Channel channel = session.getChannel();
        MqttMessage mqttMessage = ProtocolMessageUtil.createMqttGwCmdReplyMsg(gatewayCmdMessage, data);
        channel.writeAndFlush(mqttMessage);
    }
}
