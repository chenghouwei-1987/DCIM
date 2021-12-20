package com.cmiot.gateway.protocolhub.handler;

import com.cmiot.gateway.entity.DeviceSession;
import com.cmiot.gateway.entity.ProtocolType;
import com.cmiot.gateway.entity.DeviceMessage;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.protocolhub.DevSessionManager;
import com.cmiot.gateway.utils.DeviceSessionNettyUtils;
import com.cmiot.gateway.utils.ExtensionUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.GW_DOWN_LINK;
import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.LOGOUT;

/**
 * 下行消息处理
 */
@Slf4j
public final class DownLinkChannelHandler {

    private ILogger logger = ExtensionUtils.getLogger();

    /**
     * 推送消息给设备
     *
     * @param deviceMessage protocolMessage
     * @return 是否发送成功，仅关心发送本身，不关心发送结果
     */
    public boolean pushToDevice(DeviceMessage deviceMessage) {
        long productId = deviceMessage.getProductId();
        String deviceId = deviceMessage.getDeviceId();
        DeviceSession deviceSession = DevSessionManager.getDevSession(deviceId);
        // 无效设备
        if (deviceSession == null || deviceSession.getChannel() == null) {
            logger.logDevWarn(log, GW_DOWN_LINK, productId, deviceId, "push to device failed dut to null device session");
            return false;
        }
        Channel channel = deviceSession.getChannel();
        boolean connected = deviceSession.isConnected();
        // 设备未连接
        if (!connected) {
            logger.logDevWarn(log, GW_DOWN_LINK,  productId, deviceId,
                    "push to device failed because device is not connected");
            return false;
        }
        // 目前协议接入中心仅支持TCP
        if (DeviceSessionNettyUtils.deviceProtocolType(channel) == ProtocolType.TCP) {
            if (!channel.isActive()) {
                logger.logDevWarn(log, GW_DOWN_LINK, productId, deviceId,
                        "push to device failed due to inactive channel");
                return false;
            } else {
                channel.writeAndFlush(deviceMessage);
            }
        } else {
            logger.logDevWarn(log, GW_DOWN_LINK, productId, deviceId,
                    "unsupported protocol type while pushing to device:" + DeviceSessionNettyUtils.deviceProtocolType(channel).name());
            return false;
        }
        logger.logDevInfo(log, GW_DOWN_LINK, productId, deviceId, deviceMessage.getMessageType().name());
        return true;
    }


    /**
     * 主动断开设备连接
     *
     * @param deviceMessage protocolMessage
     * @return 是否发送成功，仅关心发送本身，不关心发送结果
     */
    public boolean logoutDevice(DeviceMessage deviceMessage) {
        String deviceId = deviceMessage.getDeviceId();
        DeviceSession deviceSession = DevSessionManager.getDevSession(deviceId);
        if (deviceSession == null) {
            return true;
        }
        Channel channel = deviceSession.getChannel();
        if (channel == null || !deviceSession.isConnected()) {
            return true;
        }

        // 目前协议站仅支持TCP
        if (DeviceSessionNettyUtils.deviceProtocolType(channel) == ProtocolType.TCP) {
            if (!channel.isActive()) {
                return true;
            } else {
                channel.close();
            }
        } else {
            logger.logDevWarn(log, LOGOUT, deviceMessage.getProductId(), deviceId,
                    "unsupported protocol type while logout device: " + DeviceSessionNettyUtils.deviceProtocolType(channel).name());
            return true;
        }

        return false;
    }

}
