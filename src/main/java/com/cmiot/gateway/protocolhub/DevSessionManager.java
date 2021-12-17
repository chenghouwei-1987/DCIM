package com.cmiot.gateway.protocolhub;

import com.cmiot.gateway.entity.DeviceSession;
import com.cmiot.gateway.entity.MessageType;
import com.cmiot.gateway.entity.DeviceMessage;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.mqttadaptor.handler.UpLinkHandler;
import com.cmiot.gateway.utils.DeviceSessionNettyUtils;
import com.cmiot.gateway.utils.ExtensionUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.DISCONNECT;

/**
 * 设备Session管理
 */
@Slf4j
public final class DevSessionManager {

    private static ILogger logger = ExtensionUtils.getLogger();

    private static UpLinkHandler upLinkHandler = new UpLinkHandler();

    /**
     * 设备连接Session池
     * 设备ID与设备连接Session的映射
     */
    private static ConcurrentMap<String, DeviceSession> devSessionPool = new ConcurrentHashMap<>();

    private DevSessionManager() {
    }

    public static ConcurrentMap<String, DeviceSession> getDevSessionPool() {
        return devSessionPool;
    }

    /**
     * 创建设备连接Session
     *
     * @param productId  产品ID
     * @param deviceId   设备ID
     * @param deviceName 设备名称
     * @param channel    连接channel
     * @return 设备连接Session
     */
    public static DeviceSession createDevSession(long productId, String deviceId, String deviceName, Channel channel) {
        return DeviceSession.builder()
                .productId(productId)
                .deviceId(deviceId)
                .deviceName(deviceName)
                .channel(channel)
                .connected(false)
                .build();
    }

    /**
     * 放入设备连接Session
     *
     * @param deviceSession 设备连接Session
     */
    public static void putDevSession(DeviceSession deviceSession) {
        devSessionPool.put(deviceSession.getDeviceId(), deviceSession);
    }

    /**
     * 获取设备连接Session
     *
     * @param deviceId 设备ID
     * @return 设备连接Session
     */
    public static DeviceSession getDevSession(String deviceId) {
        return devSessionPool.get(deviceId);
    }

    /**
     * 移除设备连接Session
     *
     * @param deviceId 设备ID
     */
    private static void removeDevSession(String deviceId) {
        devSessionPool.remove(deviceId);
    }

    /**
     * 连接断开情况处理
     */
    public static void handleConnectionLost(DeviceSession devSession) {
        if (devSession != null) {
            if (devSession.isConnected()) {
                DeviceMessage deviceMessage = DeviceMessage.builder()
                        .productId(devSession.getProductId())
                        .deviceId(devSession.getDeviceId())
                        .deviceName(devSession.getDeviceName())
                        .messageType(MessageType.LOGOUT)
                        .build();
                // 主动下线
                upLinkHandler.handleMessage(deviceMessage);
                devSession.setConnected(false);
            }
            Channel channel = devSession.getChannel();
            String reason = DeviceSessionNettyUtils.deviceCloseReason(channel);
            String did = devSession.getDeviceId();
            // 移除DevSessionManager中的DeviceSession
            DevSessionManager.removeDevSession(did);
            // 移除ProxySession中的DeviceSession
            devSession.getProxySession().removeDevSession(devSession.getProductId(), devSession.getDeviceName());
            logger.logDevInfo(log, DISCONNECT, devSession.getProductId(), did, reason);
        }
    }

}
