package com.cmiot.gateway.utils;

import com.cmiot.gateway.entity.DeviceSession;
import com.cmiot.gateway.entity.ProtocolType;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * 设备连接Session Netty Channel线程绑定工具类
 */
public final class DeviceSessionNettyUtils {

    private static final String DEVICE_SESSION = "deviceSession";
    private static final String DEVICE_CLOSE_REASON = "deviceCloseReason";
    private static final String DEVICE_PROTOCOL_TYPE = "deviceProtocolType";
    private static final String DEVICE_REMOTE_ADDRESS = "deviceRemoteAddress";

    private static final AttributeKey<DeviceSession> ATTR_KEY_DEVICE_SESSION = AttributeKey.valueOf(DEVICE_SESSION);
    private static final AttributeKey<String> ATTR_KEY_CLOSE_REASON = AttributeKey.valueOf(DEVICE_CLOSE_REASON);
    private static final AttributeKey<ProtocolType> ATTR_KEY_PROTOCOL_TYPE = AttributeKey.valueOf(DEVICE_PROTOCOL_TYPE);
    private static final AttributeKey<String> ATTR_KEY_REMOTE_ADDRESS = AttributeKey.valueOf(DEVICE_REMOTE_ADDRESS);

    private DeviceSessionNettyUtils() {
    }

    public static void setDeviceSession(Channel channel, DeviceSession deviceSession) {
        channel.attr(DeviceSessionNettyUtils.ATTR_KEY_DEVICE_SESSION).set(deviceSession);
    }

    public static DeviceSession deviceSession(Channel channel) {
        DeviceSession deviceSession = null;
        if (null != channel.attr(DeviceSessionNettyUtils.ATTR_KEY_DEVICE_SESSION).get()) {
            deviceSession = channel.attr(DeviceSessionNettyUtils.ATTR_KEY_DEVICE_SESSION).get();
        }
        return deviceSession;
    }

    public static void setDeviceCloseReason(Channel channel, String closeReason) {
        channel.attr(DeviceSessionNettyUtils.ATTR_KEY_CLOSE_REASON).set(closeReason);
    }

    public static String deviceCloseReason(Channel channel) {
        String closeReason = "close by device actively";
        if (null != channel.attr(DeviceSessionNettyUtils.ATTR_KEY_CLOSE_REASON).get()) {
            closeReason = channel.attr(DeviceSessionNettyUtils.ATTR_KEY_CLOSE_REASON).get();
        }
        return closeReason;
    }

    public static void setDeviceProtocolType(Channel channel, ProtocolType protocolType) {
        channel.attr(DeviceSessionNettyUtils.ATTR_KEY_PROTOCOL_TYPE).set(protocolType);
    }

    public static ProtocolType deviceProtocolType(Channel channel) {
        ProtocolType protocolType = null;
        if (null != channel.attr(DeviceSessionNettyUtils.ATTR_KEY_PROTOCOL_TYPE).get()) {
            protocolType = channel.attr(DeviceSessionNettyUtils.ATTR_KEY_PROTOCOL_TYPE).get();
        }
        return protocolType;
    }

    public static void setDeviceRemoteAddress(Channel channel, String remoteAddress) {
        channel.attr(DeviceSessionNettyUtils.ATTR_KEY_REMOTE_ADDRESS).set(remoteAddress);
    }

    public static String deviceRemoteAddress(Channel channel) {
        String remoteAddress = null;
        if (null != channel.attr(DeviceSessionNettyUtils.ATTR_KEY_REMOTE_ADDRESS).get()) {
            remoteAddress = channel.attr(DeviceSessionNettyUtils.ATTR_KEY_REMOTE_ADDRESS).get();
        }
        return remoteAddress;
    }
}
