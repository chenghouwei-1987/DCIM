package com.cmiot.gateway.mqttadaptor.handler;

import com.alibaba.fastjson.JSON;
import com.cmiot.gateway.custom.DownLinkHandler;
import com.cmiot.gateway.entity.*;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.mqttadaptor.ControlSessionManager;
import com.cmiot.gateway.mqttadaptor.ProxySessionManager;
import com.cmiot.gateway.mqttadaptor.codec.ProtocolMsgDecoder;
import com.cmiot.gateway.mqttadaptor.mqtt.MqttSubscription;
import com.cmiot.gateway.utils.ConnectSessionNettyUtils;
import com.cmiot.gateway.utils.DeviceSessionNettyUtils;
import com.cmiot.gateway.utils.ExtensionUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.PLATFORM_DOWN_LINK;
import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.RUNTIME;
import static io.netty.channel.ChannelFutureListener.CLOSE_ON_FAILURE;


/**
 * 内部消息处理Handler
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public final class ProtocolMessageHandler extends SimpleChannelInboundHandler<MqttMessage> {

    private ILogger logger = ExtensionUtils.getLogger();

    private static final String SUBSCRIBE_FORMAT = "$gw-proxy/%d/%s/#";

    private final DownLinkHandler downLinkHandler;

    public ProtocolMessageHandler(DownLinkHandler downLinkHandler) {
        this.downLinkHandler = downLinkHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) {
        ConnectionType connectionType = ConnectSessionNettyUtils.connectionType(channelHandlerContext.channel());
        switch (connectionType) {
            case PROXY_CONNECTION:
                DeviceMessage deviceMessage = ProtocolMsgDecoder.decodeDeviceMsg(mqttMessage);
                ProxySession proxySession = ConnectSessionNettyUtils.proxySession(channelHandlerContext.channel());
                if (proxySession != null) {
                    DeviceSession deviceSession = proxySession.getDevSession(deviceMessage.getProductId(), deviceMessage.getDeviceName());
                    if (null != deviceSession) {
                        String deviceId = deviceSession.getDeviceId();
                        //decode得到的ProtocolMessage无设备id
                        deviceMessage.setDeviceId(deviceId);
                        dispatchProxyConnMessage(deviceSession, deviceMessage);
                    }
                }
                break;
            case CONTROL_CONNECTION:
                GatewayMessage gatewayMessage = ProtocolMsgDecoder.decodeGatewayMsg(mqttMessage);
                if (ControlSessionManager.isConnected()) {
                    dispatchCtrlConnMessage(gatewayMessage);

                }
                break;
            default:
                logger.logInnerWarn(log, RUNTIME, "unexpected connection type:" + connectionType);
                break;
        }

    }

    /**
     * 平台下行控制连接消息分发回调
     *
     * @param gatewayMessage 控制连接内部数据消息
     */
    private void dispatchCtrlConnMessage(GatewayMessage gatewayMessage) {
        MessageType messageType = gatewayMessage.getMessageType();
        switch (messageType) {
            case DOWN_LINK_GW_CMD:
                downLinkHandler.onGwCmdRequest((GatewayCmdMessage) gatewayMessage);
                break;
            case GW_CMD_REPLY_ACCEPTED_RESP:
                downLinkHandler.onGwCmdResponseAccepted((GatewayCmdMessage) gatewayMessage);
                break;
            case GW_CMD_REPLY_REJECTED_RESP:
                GatewayCmdMessage cmdMessage = (GatewayCmdMessage) gatewayMessage;
                String payloadInString = new String(gatewayMessage.getData());
                DownLinkCmdErrorMessage errorMessage = JSON.parseObject(payloadInString, DownLinkCmdErrorMessage.class);
                errorMessage.setCmdId(cmdMessage.getCmdId());
                downLinkHandler.onGwCmdResponseRejected(cmdMessage, errorMessage);
                break;
            default:
                logger.logCtrlConnWarn(log, PLATFORM_DOWN_LINK, gatewayMessage.getSvcId(), gatewayMessage.getInstName(), "unrecognized down link message type:" + messageType);
                break;
        }
    }

    /**
     * 平台下行代理连接消息分发回调
     *
     * @param deviceSession    设备session
     * @param deviceMessage 代理连接内部数据消息
     */
    private void dispatchProxyConnMessage(DeviceSession deviceSession, DeviceMessage deviceMessage) {
        MessageType messageType = deviceMessage.getMessageType();
        switch (messageType) {
            case LOGIN_ACCEPTED_RESPONSE:
                long productId = deviceMessage.getProductId();
                String deviceName = deviceMessage.getDeviceName();
                //登录前先订阅设备的topic
                List<MqttSubscription> subscriptionList = new ArrayList<>();
                subscriptionList.add(new MqttSubscription(MqttQoS.AT_MOST_ONCE,
                        String.format(SUBSCRIBE_FORMAT, productId, deviceName)));

                deviceSession.getProxySession().getMqttClient().subscribe(subscriptionList)
                        .addListener(future ->
                                downLinkHandler.onDeviceLoginSuccess(deviceMessage));
                break;
            case LOGIN_REJECTED_RESPONSE: {
                String payloadInString = new String(deviceMessage.getData());
                DownLinkErrorMessage errorMessage = JSON.parseObject(payloadInString, DownLinkErrorMessage.class);
                boolean proxiedDevicesReachedLimit = ProxySessionManager.isProxiedDevicesReachedLimit(deviceSession, deviceMessage, errorMessage);
                if (!proxiedDevicesReachedLimit) {
                    DeviceSessionNettyUtils.setDeviceCloseReason(deviceSession.getChannel(), errorMessage.getErrMsg());
                    deviceSession.getProxySession().setDevicesReachedLimit(false);
                    downLinkHandler.onDeviceLoginFailed(deviceMessage, errorMessage);
                }
                break;
            }
            case LOGOUT_ACCEPTED_RESPONSE:
                downLinkHandler.onDeviceLogoutSuccess(deviceMessage);
                deviceSession.getProxySession().setDevicesReachedLimit(false);
                break;
            case LOGOUT_NOTIFY_RESPONSE: {
                String payloadInString = new String(deviceMessage.getData());
                DownLinkErrorMessage errorMessage = JSON.parseObject(payloadInString, DownLinkErrorMessage.class);
                if (deviceSession != null) {
                    deviceSession.getProxySession().setDevicesReachedLimit(false);
                }
                downLinkHandler.onDeviceNotifiedLogout(deviceMessage, errorMessage);
                break;
            }
            case DP_ACCEPTED_RESPONSE:
                downLinkHandler.onDeviceDpAccepted(deviceMessage);
                break;
            case DP_REJECTED_RESPONSE: {
                String payloadInString = new String(deviceMessage.getData());
                DownLinkDpErrorMessage errorMessage = JSON.parseObject(payloadInString, DownLinkDpErrorMessage.class);
                downLinkHandler.onDeviceDpRejected(deviceMessage, errorMessage);
                break;
            }
            case DOWN_LINK_CMD:
                downLinkHandler.onDeviceCmdRequest((DeviceCmdMessage) deviceMessage);
                break;
            case CMD_REPLY_ACCEPTED_RESPONSE:
                downLinkHandler.onDeviceCmdResponseAccepted((DeviceCmdMessage) deviceMessage);
                break;
            case CMD_REPLY_REJECTED_RESPONSE: {
                DeviceCmdMessage cmdMessage = (DeviceCmdMessage) deviceMessage;
                String payloadInString = new String(deviceMessage.getData());
                DownLinkCmdErrorMessage errorMessage = JSON.parseObject(payloadInString, DownLinkCmdErrorMessage.class);
                errorMessage.setCmdId(cmdMessage.getCmdId());
                downLinkHandler.onDeviceCmdResponseRejected(deviceMessage, errorMessage);
                break;
            }
            case IMAGE_UPDATE_ACCEPTED_RESPONSE:
                downLinkHandler.onDeviceImageUpdateAccepted(deviceMessage);
                break;
            case IMAGE_UPDATE_REJECTED_RESPONSE: {
                String payloadInString = new String(deviceMessage.getData());
                DownLinkErrorMessage errorMessage = JSON.parseObject(payloadInString, DownLinkErrorMessage.class);
                downLinkHandler.onDeviceImageUpdateRejected(deviceMessage, errorMessage);
                break;
            }
            case IMAGE_GET_ACCEPTED_RESPONSE:
                downLinkHandler.onDeviceImageGetAccepted(deviceMessage);
                break;
            case IMAGE_GET_REJECTED_RESPONSE: {
                String payloadInString = new String(deviceMessage.getData());
                DownLinkErrorMessage errorMessage = JSON.parseObject(payloadInString, DownLinkErrorMessage.class);
                downLinkHandler.onDeviceImageGetRejected(deviceMessage, errorMessage);
                break;
            }
            case IMAGE_DELTA:
                downLinkHandler.onDeviceImageDelta(deviceMessage);
                break;
            default:
                logger.logPxyConnWarn(log, PLATFORM_DOWN_LINK, "unrecognized down link message type:" + messageType, null);
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ConnectionType connectionType = ConnectSessionNettyUtils.connectionType(ctx.channel());
        switch (connectionType) {
            case CONTROL_CONNECTION:
                ControlSessionManager.handleConnectionLost();
                break;
            case PROXY_CONNECTION:
                ProxySession proxySession = ConnectSessionNettyUtils.proxySession(ctx.channel());
                if (proxySession != null) {
                    ProxySessionManager.handleConnectionLost(proxySession);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.logInnerError(log, RUNTIME, "exceptionCaught cause:", cause);
        ctx.close().addListener(CLOSE_ON_FAILURE);
    }
}
