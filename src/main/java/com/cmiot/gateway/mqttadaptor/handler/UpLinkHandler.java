package com.cmiot.gateway.mqttadaptor.handler;

import com.cmiot.gateway.entity.DeviceMessage;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.utils.ExtensionUtils;

/**
 * 上行数据处理Handler，用于处理设备上行OneNET数据，需要开发者实现业务逻辑
 */
public class UpLinkHandler {

    private ILogger logger = ExtensionUtils.getLogger();

    private UpLinkChannelHandler upLinkChannelHandler = new UpLinkChannelHandler();

    /**
     * 处理ProtocolMessage
     *
     * @param message message
     */
    public void handleMessage(DeviceMessage message) {
        switch (message.getMessageType()) {
            case LOGIN:
                upLinkChannelHandler.doDeviceOnline(message);
                break;
            case LOGOUT:
                upLinkChannelHandler.doDeviceOffline(message);
                break;
            default:
                upLinkChannelHandler.doPublish(message);
                break;
        }
    }
}
