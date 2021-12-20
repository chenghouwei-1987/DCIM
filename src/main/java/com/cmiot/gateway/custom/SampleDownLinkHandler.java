package com.cmiot.gateway.custom;

import com.cmiot.gateway.entity.*;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.mqttadaptor.handler.UpLinkChannelHandler;
import com.cmiot.gateway.protocolhub.handler.DownLinkChannelHandler;
import com.cmiot.gateway.utils.ExtensionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.*;

/**
 * {@link DownLinkHandler}的实现范例
 * <p>
 * 如不使用本实现，请删除此类或删除@Component注解
 */
@Slf4j
@Component
public final class SampleDownLinkHandler implements DownLinkHandler {

    private ILogger logger = ExtensionUtils.getLogger();

    /**
     * 用于推送消息给设备和主动断开设备连接
     */
    private DownLinkChannelHandler downLinkChannelHandler = new DownLinkChannelHandler();
    private UpLinkChannelHandler upLinkChannelHandler = new UpLinkChannelHandler();

    @Override
    public void onDeviceLoginSuccess(DeviceMessage deviceMessage) {
        logger.logDevInfo(log, LOGIN, deviceMessage.getProductId(), deviceMessage.getDeviceId(), "success");
        // 主动推送消息给代理设备
        downLinkChannelHandler.pushToDevice(deviceMessage);
    }

    @Override
    public void onDeviceLoginFailed(DeviceMessage deviceMessage, DownLinkErrorMessage errorMessage) {
        logger.logDevWarn(log, LOGIN, deviceMessage.getProductId(), deviceMessage.getDeviceId(), "failed");
    }

    @Override
    public void onDeviceLogoutSuccess(DeviceMessage deviceMessage) {
        logger.logDevInfo(log, LOGOUT, deviceMessage.getProductId(), deviceMessage.getDeviceId(), "success");
    }

    @Override
    public void onDeviceNotifiedLogout(DeviceMessage deviceMessage, DownLinkErrorMessage errorMessage) {
        logger.logDevWarn(log, LOGOUT, deviceMessage.getProductId(), deviceMessage.getDeviceId(), "logout notified, errMsg:" + errorMessage.getErrMsg());
    }

    @Override
    public void onDeviceDpAccepted(DeviceMessage deviceMessage) {
        logger.logDevInfo(log, GW_UP_LINK, deviceMessage.getProductId(), deviceMessage.getDeviceId(), "upload dp success");
    }

    @Override
    public void onDeviceDpRejected(DeviceMessage deviceMessage, DownLinkDpErrorMessage errorMessage) {
        logger.logDevWarn(log, GW_UP_LINK, deviceMessage.getProductId(), deviceMessage.getDeviceId(), "upload dp failed, errMsg:" + errorMessage.getErrMsg());
    }

    @Override
    public void onDeviceCmdRequest(DeviceCmdMessage deviceCmdMessage) {
        logger.logDevInfo(log, PLATFORM_DOWN_LINK, deviceCmdMessage.getProductId(), deviceCmdMessage.getDeviceId(), "cmd received", "cmdId:" + deviceCmdMessage.getCmdId());
        downLinkChannelHandler.pushToDevice(deviceCmdMessage);
    }

    @Override
    public void onDeviceCmdResponseAccepted(DeviceCmdMessage deviceCmdMessage) {
        logger.logDevInfo(log, PLATFORM_DOWN_LINK, deviceCmdMessage.getProductId(), deviceCmdMessage.getDeviceId(), "reply cmd success", "cmdId:" + deviceCmdMessage.getCmdId());
    }

    @Override
    public void onDeviceCmdResponseRejected(DeviceMessage deviceMessage, DownLinkCmdErrorMessage errorMessage) {
        logger.logDevWarn(log, PLATFORM_DOWN_LINK, deviceMessage.getProductId(), deviceMessage.getDeviceId(), "reply cmd failed, errMsg:" + errorMessage.getErrMsg(), "cmdId:" + errorMessage.getCmdId());
    }

    @Override
    public void onDeviceImageUpdateAccepted(DeviceMessage deviceMessage) {
        logger.logDevInfo(log, PLATFORM_DOWN_LINK, deviceMessage.getProductId(), deviceMessage.getDeviceId(), "update image success");
    }

    @Override
    public void onDeviceImageUpdateRejected(DeviceMessage deviceMessage, DownLinkErrorMessage errorMessage) {
        logger.logDevWarn(log, PLATFORM_DOWN_LINK, deviceMessage.getProductId(), deviceMessage.getDeviceId(), "update image failed, errMsg:" + errorMessage.getErrMsg());
    }

    @Override
    public void onDeviceImageGetAccepted(DeviceMessage deviceMessage) {
        logger.logDevInfo(log, PLATFORM_DOWN_LINK, deviceMessage.getProductId(), deviceMessage.getDeviceId(), "get image success");
    }

    @Override
    public void onDeviceImageGetRejected(DeviceMessage deviceMessage, DownLinkErrorMessage errorMessage) {
        logger.logDevWarn(log, PLATFORM_DOWN_LINK, deviceMessage.getProductId(), deviceMessage.getDeviceId(), "get image failed, errMsg:" + errorMessage.getErrMsg());
    }

    @Override
    public void onDeviceImageDelta(DeviceMessage deviceMessage) {
        logger.logDevInfo(log, PLATFORM_DOWN_LINK, deviceMessage.getProductId(), deviceMessage.getDeviceId(), "received image delta");
    }

    @Override
    public void onGwCmdRequest(GatewayCmdMessage gatewayCmdMessage) {
        logger.logCtrlConnInfo(log, PLATFORM_DOWN_LINK, gatewayCmdMessage.getSvcId(), gatewayCmdMessage.getInstName(), "receive gateway cmd", "cmdId:" + gatewayCmdMessage.getCmdId());
        upLinkChannelHandler.doGwCmdReply(gatewayCmdMessage, gatewayCmdMessage.getData());
    }

    @Override
    public void onGwCmdResponseAccepted(GatewayCmdMessage gatewayCmdMessage) {
        logger.logCtrlConnInfo(log, PLATFORM_DOWN_LINK, gatewayCmdMessage.getSvcId(), gatewayCmdMessage.getInstName(), "reply gateway cmd success", "cmdId:" + gatewayCmdMessage.getCmdId());
    }

    @Override
    public void onGwCmdResponseRejected(GatewayCmdMessage gatewayCmdMessage, DownLinkCmdErrorMessage errorMessage) {
        logger.logCtrlConnWarn(log, PLATFORM_DOWN_LINK, gatewayCmdMessage.getSvcId(), gatewayCmdMessage.getInstName(), "reply gateway cmd failed, errMsg:" + errorMessage.getErrMsg(), "cmdId:" + gatewayCmdMessage.getCmdId());
    }

}
