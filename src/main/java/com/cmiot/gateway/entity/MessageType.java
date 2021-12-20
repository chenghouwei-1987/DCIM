package com.cmiot.gateway.entity;

/**
 * 消息类型，用于MQTT Message (平台通信) 和 协议网关服务内部消息之间的转换
 */
public enum MessageType {

    /**
     *  登录代理设备
     */
    LOGIN,

    /**
     * 登出代理设备
     */
    LOGOUT,

    /**
     * 代理设备上传数据点
     */
    DP,

    /**
     * 代理设备回复平台命令
     */
    CMD_REPLY,

    /**
     * 代理设备更新镜像
     */
    IMAGE_UPDATE,

    /**
     * 代理设备获取镜像
     */
    IMAGE_GET,
    //end of uplink message type


    //begin down link message type
    /**
     * 代理设备登录成功响应
     */
    LOGIN_ACCEPTED_RESPONSE,

    /**
     * 代理设备登录失败响应
     */
    LOGIN_REJECTED_RESPONSE,

    /**
     * 代理设备登出成功响应
     */
    LOGOUT_ACCEPTED_RESPONSE,

    /**
     * 代理设备被平台主动登出的响应
     */
    LOGOUT_NOTIFY_RESPONSE,

    /**
     * 代理设备上传数据点成功响应
     */
    DP_ACCEPTED_RESPONSE,

    /**
     * 代理设备上传数据点失败响应
     */
    DP_REJECTED_RESPONSE,

    /**
     * 代理设备获取平台下发命令
     */
    DOWN_LINK_CMD,

    /**
     * 代理设备回复命令成功后的平台响应
     */
    CMD_REPLY_ACCEPTED_RESPONSE,

    /**
     * 代理设备回复命令失败后的平台响应
     */
    CMD_REPLY_REJECTED_RESPONSE,

    /**
     * 平台下发给网关的命令
     */
    DOWN_LINK_GW_CMD,

    /**
     * 网关回复命令成功后的平台响应
     */
    GW_CMD_REPLY_ACCEPTED_RESP,

    /**
     * 网关回复命令失败后的平台响应
     */
    GW_CMD_REPLY_REJECTED_RESP,

    /**
     * 代理设备更新镜像成功响应
     */
    IMAGE_UPDATE_ACCEPTED_RESPONSE,

    /**
     * 代理设备更新镜像失败响应
     */
    IMAGE_UPDATE_REJECTED_RESPONSE,

    /**
     * 代理设备获取镜像成功响应
     */
    IMAGE_GET_ACCEPTED_RESPONSE,

    /**
     * 代理设备获取镜像失败响应
     */
    IMAGE_GET_REJECTED_RESPONSE,

    /**
     * 代理设备获取下发的镜像delta消息
     */
    IMAGE_DELTA,
    //end of down link message type

    /**
     * 未知类型
     */
    UNKNOWN
}
