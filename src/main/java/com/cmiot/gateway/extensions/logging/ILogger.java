package com.cmiot.gateway.extensions.logging;


import org.slf4j.Logger;

public interface ILogger {
    /**
     * Inner事件info日志
     *
     * @param log    log
     * @param action 行为
     * @param desc   描述
     */
    void logInnerInfo(Logger log, LoggerFormat.Action action, String desc);

    /**
     * Inner事件info日志
     *
     * @param log      log
     * @param action   行为
     * @param svcId    网关服务id
     * @param instName 网关实例名
     * @param desc     描述
     */
    void logInnerInfo(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc);

    /**
     * Inner事件warn日志
     *
     * @param log    log
     * @param action 行为
     * @param desc   描述
     */
    void logInnerWarn(Logger log, LoggerFormat.Action action, String desc);

    /**
     * Inner事件warn日志
     *
     * @param log      log
     * @param action   行为
     * @param svcId    网关服务id
     * @param instName 网关实例名
     * @param desc     描述
     */
    void logInnerWarn(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc);

    /**
     * Inner事件error日志
     *
     * @param log    log
     * @param action 行为
     * @param desc   描述
     * @param e      e
     */
    void logInnerError(Logger log, LoggerFormat.Action action, String desc, Throwable e);

    /**
     * Inner事件error日志
     *
     * @param log      log
     * @param action   行为
     * @param svcId    网关服务id
     * @param instName 网关实例名
     * @param desc     描述
     * @param e        e
     */
    void logInnerError(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc, Throwable e);

    /**
     * ProtocoHub事件info日志
     *
     * @param log    log
     * @param action 行为
     * @param desc   描述
     */
    void logProtocolHubInfo(Logger log, LoggerFormat.Action action, String desc);

    /**
     * ProtocoHub事件info日志
     *
     * @param log    log
     * @param action 行为
     * @param extras 附加信息，以key:value,key:value的形式描述
     * @param desc   描述
     */
    void logProtocolHubInfo(Logger log, LoggerFormat.Action action, String extras, String desc);

    /**
     * ProtocoHub事件warn日志
     *
     * @param log    log
     * @param action 行为
     * @param desc   描述
     */
    void logProtocolHubWarn(Logger log, LoggerFormat.Action action, String desc);

    /**
     * ProtocoHub事件warn日志
     *
     * @param log    log
     * @param action 行为
     * @param extras 附加信息，以key:value,key:value的形式描述
     * @param desc   描述
     */
    void logProtocolHubWarn(Logger log, LoggerFormat.Action action, String extras, String desc);

    /**
     * ProtocoHub事件error日志
     *
     * @param log    log
     * @param action 行为
     * @param desc   描述
     * @param e      e
     */
    void logProtocolHubError(Logger log, LoggerFormat.Action action, String desc, Throwable e);

    /**
     * ProtocoHub事件error日志
     *
     * @param log    log
     * @param action 行为
     * @param extras 附加信息，以key:value,key:value的形式描述
     * @param desc   描述
     * @param e      e
     */
    void logProtocolHubError(Logger log, LoggerFormat.Action action, String extras, String desc, Throwable e);

    /**
     * PxyConn事件info日志
     *
     * @param log     log
     * @param action  行为
     * @param desc    描述
     * @param proxyId 代理连接接入mqtt接入机的clientId
     */
    void logPxyConnInfo(Logger log, LoggerFormat.Action action, String desc, String proxyId);

    /**
     * PxyConn事件info日志
     *
     * @param log     log
     * @param action  行为
     * @param desc    描述
     * @param proxyId 代理连接接入mqtt接入机的clientId
     * @param extras  附加信息，以key:value,key:value的形式描述
     */
    void logPxyConnInfo(Logger log, LoggerFormat.Action action, String desc, String proxyId, String extras);

    /**
     * PxyConn事件warn日志
     *
     * @param log     log
     * @param action  行为
     * @param desc    描述
     * @param proxyId 代理连接接入mqtt接入机的clientId
     */
    void logPxyConnWarn(Logger log, LoggerFormat.Action action, String desc, String proxyId);

    /**
     * PxyConn事件warn日志
     *
     * @param log     log
     * @param action  行为
     * @param desc    描述
     * @param proxyId 代理连接接入mqtt接入机的clientId
     * @param extras  附加信息，以key:value,key:value的形式描述
     */
    void logPxyConnWarn(Logger log, LoggerFormat.Action action, String desc, String proxyId, String extras);

    /**
     * PxyConn事件error日志
     *
     * @param log     log
     * @param action  行为
     * @param desc    描述
     * @param proxyId 代理连接接入mqtt接入机的clientId
     * @param e       e
     */
    void logPxyConnError(Logger log, LoggerFormat.Action action, String desc, String proxyId, Throwable e);

    /**
     * PxyConn事件error日志
     *
     * @param log     log
     * @param action  行为
     * @param desc    描述
     * @param proxyId 代理连接接入mqtt接入机的clientId
     * @param extras  附加信息，以key:value,key:value的形式描述
     * @param e       e
     */
    void logPxyConnError(Logger log, LoggerFormat.Action action, String desc, String proxyId, String extras, Throwable e);

    /**
     * CtrlConn事件info日志
     *
     * @param log      log
     * @param action   行为
     * @param svcId    网关服务id
     * @param instName 网关实例名
     * @param desc     描述
     */
    void logCtrlConnInfo(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc);

    /**
     * CtrlConn事件info日志
     *
     * @param log      log
     * @param action   行为
     * @param svcId    网关服务id
     * @param instName 网关实例名
     * @param desc     描述
     * @param extras   附加信息，以key:value,key:value的形式描述
     */
    void logCtrlConnInfo(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc, String extras);

    /**
     * CtrlConn事件warn日志
     *
     * @param log      log
     * @param action   行为
     * @param svcId    网关服务id
     * @param instName 网关实例名
     * @param desc     描述
     */
    void logCtrlConnWarn(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc);

    /**
     * CtrlConn事件warn日志
     *
     * @param log      log
     * @param action   行为
     * @param svcId    网关服务id
     * @param instName 网关实例名
     * @param desc     描述
     * @param extras   附加信息，以key:value,key:value的形式描述
     */
    void logCtrlConnWarn(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc, String extras);

    /**
     * CtrlConn事件error日志
     *
     * @param log      log
     * @param action   行为
     * @param svcId    网关服务id
     * @param instName 网关实例名
     * @param desc     描述
     * @param e        e
     */
    void logCtrlConnError(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc, Throwable e);

    /**
     * CtrlConn事件error日志
     *
     * @param log      log
     * @param action   行为
     * @param svcId    网关服务id
     * @param instName 网关实例名
     * @param desc     描述
     * @param extras   附加信息，以key:value,key:value的形式描述
     * @param e        e
     */
    void logCtrlConnError(Logger log, LoggerFormat.Action action, String svcId, String instName, String desc, String extras, Throwable e);

    /**
     * Dev事件info日志
     *
     * @param log    log
     * @param action 行为
     * @param pid    产品id
     * @param did    设备id
     * @param desc   描述
     */
    void logDevInfo(Logger log, LoggerFormat.Action action, Long pid, String did, String desc);

    /**
     * Dev事件info日志
     *
     * @param log    log
     * @param action 行为
     * @param pid    产品id
     * @param did    设备id
     * @param desc   描述
     * @param extras 附加信息，以key:value,key:value的形式描述
     */
    void logDevInfo(Logger log, LoggerFormat.Action action, Long pid, String did, String desc, String extras);

    /**
     * Dev事件warn日志
     *
     * @param log    log
     * @param action 行为
     * @param pid    产品id
     * @param did    设备id
     * @param desc   描述
     */
    void logDevWarn(Logger log, LoggerFormat.Action action, Long pid, String did, String desc);

    /**
     * Dev事件warn日志
     *
     * @param log    log
     * @param action 行为
     * @param pid    产品id
     * @param did    设备id
     * @param desc   描述
     * @param extras 附加信息，以key:value,key:value的形式描述
     */
    void logDevWarn(Logger log, LoggerFormat.Action action, Long pid, String did, String desc, String extras);

    /**
     * Dev事件error日志
     *
     * @param log    log
     * @param action 行为
     * @param pid    产品id
     * @param did    设备id
     * @param desc   描述
     * @param e      e
     */
    void logDevError(Logger log, LoggerFormat.Action action, Long pid, String did, String desc, Throwable e);

    /**
     * Dev事件error日志
     *
     * @param log    log
     * @param action 行为
     * @param pid    产品id
     * @param did    设备id
     * @param desc   描述
     * @param extras 附加信息，以key:value,key:value的形式描述
     * @param e      e
     */
    void logDevError(Logger log, LoggerFormat.Action action, Long pid, String did, String desc, String extras, Throwable e);


    /**
     * Metric事件info日志
     *
     * @param log      log
     * @param action   行为
     * @param svcId    网关服务id
     * @param instName 网关实例名
     * @param extras   附加信息，以key:value,key:value的形式描述
     * @param desc     描述
     */
    void logMetricInfo(Logger log, LoggerFormat.Action action, String svcId, String instName, String extras, String desc);

}
