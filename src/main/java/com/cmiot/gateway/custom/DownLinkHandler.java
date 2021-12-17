package com.cmiot.gateway.custom;

import com.cmiot.gateway.entity.*;

/**
 * 下行数据处理接口，用于接收并处理OneNET接入机下行数据
 * <p>
 * * 可使用{@link  com.cmiot.gateway.protocolhub.handler.DownLinkChannelHandler#pushToDevice(DeviceMessage)} 推送消息给设备
 */
public interface DownLinkHandler {

    /**
     * 设备登陆成功
     *
     * @param deviceMessage protocolMessage
     */
    void onDeviceLoginSuccess(DeviceMessage deviceMessage);

    /**
     * 设备登陆失败
     *
     * @param errorMessage 相关错误信息
     */
    void onDeviceLoginFailed(DeviceMessage deviceMessage, DownLinkErrorMessage errorMessage);

    /**
     * 设备主动登出成功
     *
     * @param deviceMessage protocolMessage
     */
    void onDeviceLogoutSuccess(DeviceMessage deviceMessage);

    /**
     * 平台主动登出设备
     *
     * @param errorMessage 相关错误信息
     */
    void onDeviceNotifiedLogout(DeviceMessage deviceMessage, DownLinkErrorMessage errorMessage);

    /**
     * 平台接受设备数据点上传
     *
     * @param deviceMessage protocolMessage
     */
    void onDeviceDpAccepted(DeviceMessage deviceMessage);

    /**
     * 平台拒绝设备数据点上传
     *
     * @param errorMessage 相关错误信息
     */
    void onDeviceDpRejected(DeviceMessage deviceMessage, DownLinkDpErrorMessage errorMessage);

    /**
     * 收到平台下行命令消息
     *
     * @param protocolCmdMessage protocolMessage
     */
    void onDeviceCmdRequest(DeviceCmdMessage protocolCmdMessage);

    /**
     * 平台接收设备命令回复
     *
     * @param protocolMessage protocolMessage
     */
    void onDeviceCmdResponseAccepted(DeviceCmdMessage protocolMessage);

    /**
     * 平台拒绝设备命令回复
     *
     * @param deviceMessage protocolMessage
     * @param errorMessage    相关错误信息
     */
    void onDeviceCmdResponseRejected(DeviceMessage deviceMessage, DownLinkCmdErrorMessage errorMessage);

    /**
     * 平台接收设备镜像更新请求
     *
     * @param deviceMessage protocolMessage
     */
    void onDeviceImageUpdateAccepted(DeviceMessage deviceMessage);

    /**
     * 平台拒绝设备镜像更新请求
     *
     * @param errorMessage 相关错误信息
     */
    void onDeviceImageUpdateRejected(DeviceMessage deviceMessage, DownLinkErrorMessage errorMessage);

    /**
     * 平台接收设备镜像查询请求，返回结果在{@link DeviceMessage#getData()}
     *
     * @param deviceMessage protocolMessage
     */
    void onDeviceImageGetAccepted(DeviceMessage deviceMessage);

    /**
     * 平台拒绝设备镜像查询请求
     *
     * @param deviceMessage protocolMessage
     * @param errorMessage    相关错误信息
     */
    void onDeviceImageGetRejected(DeviceMessage deviceMessage, DownLinkErrorMessage errorMessage);

    /**
     * 收到平台下行的镜像delta消息
     *
     * @param deviceMessage protocolMessage
     */
    void onDeviceImageDelta(DeviceMessage deviceMessage);

    /**
     * 收到下发给网关的命令
     * @param gatewayCmdMessage gatewayCmdMessage
     */
    void onGwCmdRequest(GatewayCmdMessage gatewayCmdMessage);

    /**
     * 网关命令回复被平台接收的回调
     *
     * @param gatewayCmdMessage gatewayCmdMessage
     */
    void onGwCmdResponseAccepted(GatewayCmdMessage gatewayCmdMessage);

    /**
     * 网关命令回复被平台拒绝的回调
     *  @param gatewayCmdMessage gatewayCmdMessage
     * @param errorMessage    相关错误信息
     */
    void onGwCmdResponseRejected(GatewayCmdMessage gatewayCmdMessage, DownLinkCmdErrorMessage errorMessage);


}
