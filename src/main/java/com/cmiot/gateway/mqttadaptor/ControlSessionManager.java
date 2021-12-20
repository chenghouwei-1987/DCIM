package com.cmiot.gateway.mqttadaptor;

import com.cmiot.gateway.config.IGatewayConfig;
import com.cmiot.gateway.entity.ConnectionType;
import com.cmiot.gateway.entity.ControlSession;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.mqttadaptor.handler.ProtocolMessageHandler;
import com.cmiot.gateway.utils.ExtensionUtils;
import com.cmiot.gateway.utils.ConnectSessionNettyUtils;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.DISCONNECT;

/**
 * 控制连接Session管理类，相同的协议网关服务实例(服务ID+实例名称)仅建立一条控制连接
 */
@Slf4j
public final class ControlSessionManager {

    private static ILogger logger = ExtensionUtils.getLogger();

    public static IGatewayConfig config = null;
    static ProtocolMessageHandler protocolMessageHandler = null;
    private static ControlSession controlSession = null;

    @Getter
    private static volatile boolean isConnected;

    private ControlSessionManager() {
    }

    public static ControlSession getSession(){
        return controlSession;
    }
    /**
     * 初始化控制连接
     *
     * @param config  网关配置
     * @param channel 控制连接channel
     * @param handler 内部数据Handler
     */
    static void initControlSession(IGatewayConfig config, Channel channel, ProtocolMessageHandler handler) {
        if (ControlSessionManager.config != null) {
            throw new IllegalStateException("duplicated initiation of control session");
        }
        ControlSessionManager.config = config;
        ControlSessionManager.protocolMessageHandler = handler;
        ControlSessionManager.controlSession = ControlSession.builder()
                .instanceName(config.getInstanceName())
                .serviceId(config.getServiceId())
                .channel(channel)
                .build();
        ControlSessionManager.isConnected = true;
        ConnectSessionNettyUtils.setConnectionType(channel, ConnectionType.CONTROL_CONNECTION);
    }

    /**
     * 重新建立控制连接
     *
     * @param channel 控制连接channel
     */
    public static void reconnectControlSession(Channel channel) {
        if (ControlSessionManager.config == null || ControlSessionManager.controlSession == null) {
            throw new IllegalStateException("control session was not initialized");
        }
        ControlSessionManager.isConnected = true;
        ControlSessionManager.controlSession.setChannel(channel);
    }

    /**
     * 判断控制连接是否存活
     *
     * @return 是否存活
     */
    static boolean isControlSessionActive() {
        return ControlSessionManager.controlSession != null &&
                ControlSessionManager.isConnected &&
                ControlSessionManager.controlSession.getChannel().isActive();
    }

    /**
     * 连接断开情况处理
     */
    public static void handleConnectionLost() {
        ControlSessionManager.isConnected = false;
        ControlSessionManager.controlSession = null;
        logger.logCtrlConnWarn(log, DISCONNECT,null,null,null);
    }
}
