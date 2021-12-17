package com.cmiot.gateway.mqttadaptor;

import com.cmiot.gateway.config.ConfigConsts;
import com.cmiot.gateway.entity.*;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.mqttadaptor.handler.UpLinkChannelHandler;
import com.cmiot.gateway.mqttadaptor.mqtt.promise.MqttConnectResult;
import com.cmiot.gateway.utils.ConnectSessionNettyUtils;
import com.cmiot.gateway.utils.DeviceSessionNettyUtils;
import com.cmiot.gateway.utils.ExtensionUtils;
import com.cmiot.gateway.utils.SasTokenGenerator;
import io.netty.channel.Channel;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.*;

/**
 * 代理连接Session管理类
 */
@Slf4j
public final class ProxySessionManager {

    private static ILogger logger = ExtensionUtils.getLogger();

    /**
     * 上一个代理连接创建时间
     */
    private static AtomicLong lastProxyConnectionCreationTime = new AtomicLong(0);
    private static UpLinkChannelHandler upLinkChannelHandler = new UpLinkChannelHandler();

    /**
     * 代理连接Session池
     * 代理连接ID与代理连接Session的映射
     */
    private static ConcurrentMap<String, ProxySession> proxySessionPool = new ConcurrentHashMap<>();

    public static ConcurrentMap<String, ProxySession> getProxySessionPool() {
        return proxySessionPool;
    }

    /**
     * 创建新的代理连接Session
     * <p>
     * 注意：仅当控制连接有效时才会建立
     *
     * @param proxyId    代理连接ID
     * @param channel    连接channel
     * @param mqttClient Mqtt Client
     * @return 新的代理连接Session
     */
    private static ProxySession createProxySession(String proxyId, Channel channel, MqttClient mqttClient) {
        ProxySession proxySession = ProxySession.builder()
                .mqttClient(mqttClient)
                .proxyId(proxyId)
                .channel(channel)
                .proxyDevAssociation(new ConcurrentHashMap<>())
                .connected(true)
                .isDevicesReachedLimit(false)
                .build();
        ConnectSessionNettyUtils.setConnectionType(channel, ConnectionType.PROXY_CONNECTION);
        ConnectSessionNettyUtils.setProxySession(channel, proxySession);
        return proxySession;
    }

    /**
     * 放入代理连接Session
     *
     * @param proxySession 代理连接Session
     */
    private static void putProxySession(ProxySession proxySession) {
        proxySessionPool.put(proxySession.getProxyId(), proxySession);
    }


    /**
     * 选择代理连接Session，用于新设备选择合适的代理连接，默认为选择当前代理设备最少的代理连接
     *
     * @return 代理连接Session
     */
    public static ProxySession chooseProxySession() {
        if (ControlSessionManager.config == null) {
            logger.logInnerWarn(log, RUNTIME, "choose proxy session failed as control session was not initialized");
            return null;
        }
        return chooseMinimumDeviceProxySession()
                .orElseGet(ProxySessionManager::initNewProxyConnection);
    }

    /**
     * 从Session池中移除
     *
     * @param proxyId 代理连接id
     */
    private static void removeProxySession(String proxyId) {
        proxySessionPool.remove(proxyId);
    }

    /**
     * 当前代理设备最少的代理连接
     *
     * @return 当前代理设备最少的代理连接
     */
    private static Optional<ProxySession> chooseMinimumDeviceProxySession() {
        return proxySessionPool.values().stream()
                .filter(proxySession -> !proxySession.isDevicesReachedLimit())
                .min(Comparator.comparingInt(ProxySession::size));
    }


    /**
     * 连接断开情况处理
     */
    public static void handleConnectionLost(ProxySession proxySession) {
        logger.logPxyConnWarn(log, DISCONNECT, null, proxySession.getProxyId());
        Iterator<Map.Entry<Pair<Long, String>, DeviceSession>> iterator = proxySession.getProxyDevAssociation().entrySet().iterator();
        // 所有代理设备下线
        while (iterator.hasNext()) {
            Map.Entry<Pair<Long, String>, DeviceSession> entry = iterator.next();
            DeviceSession deviceSession = entry.getValue();
            deviceSession.setConnected(false);
            Channel channel = deviceSession.getChannel();
            DeviceSessionNettyUtils.setDeviceCloseReason(channel, "proxy connection is disconnected");
            channel.close();
            iterator.remove();
        }
        removeProxySession(proxySession.getProxyId());
    }


    /**
     * 代理连接代理的设备数量是否超过限制
     *
     * @param deviceSession deviceSession
     * @param deviceMessage protocolMessage
     * @param errorMsg      errorMsg
     * @return 是否超过限制
     */
    public static boolean isProxiedDevicesReachedLimit(DeviceSession deviceSession, DeviceMessage deviceMessage, DownLinkErrorMessage errorMsg) {
        ProxySession proxySession = deviceSession.getProxySession();
        if (errorMsg.getErrCode() == 0x8B) {
            proxySession.setDevicesReachedLimit(true);
            deviceSession.setProxySession(null);
            // 重新选择代理连接登录
            upLinkChannelHandler.doDeviceOnline(deviceMessage);
            return true;
        }
        return false;
    }

    /**
     * 初始化代理连接，当控制连接不存在或者已经断开时不会进行初始化
     *
     * @return 代理连接Session
     */
    private static ProxySession initNewProxyConnection() {
        if (!ControlSessionManager.isControlSessionActive()) {
            logger.logPxyConnWarn(log, INIT, "init new proxy connection failed as control session is inactive", null);
            return null;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastProxyConnectionCreationTime.getAndSet(currentTimeMillis) < ConfigConsts.MAX_PROXY_CONNECTION_CREATION_INTERVAL_MS) {
            logger.logPxyConnWarn(log, INIT, "init new proxy connection failed as proxy connections create too fast", null);
            return null;
        }
        String clientId = genClientId();
        if (proxySessionPool.containsKey(clientId)) {
            logger.logPxyConnWarn(log, INIT, "existed proxy connection", clientId);
            return null;
        }
        MqttClient mqttClient = new MqttClient(ControlSessionManager.config, ControlSessionManager.protocolMessageHandler);
        MqttConnectResult result;
        try {
            String serviceId = ControlSessionManager.config.getServiceId();
            String sasToken = SasTokenGenerator.gatewaySasToken();
            if (sasToken == null) {
                logger.logPxyConnWarn(log, INIT, "init new proxy connection failed due to generate sasToken failed", clientId);
                return null;
            }
            result = mqttClient.connect(clientId, serviceId, sasToken);
        } catch (ExecutionException | InterruptedException e) {
            logger.logPxyConnWarn(log, INIT, "initialize mqtt client failed whiling choose proxy session due to " + e.getLocalizedMessage(), clientId);
            Thread.currentThread().interrupt();
            return null;
        }
        ProxySession proxySession = null;
        switch (result.returnCode()) {
            case CONNECTION_ACCEPTED:
                proxySession = createProxySession(clientId, mqttClient.getChannel(), mqttClient);
                putProxySession(proxySession);
                logger.logPxyConnInfo(log, INIT, "init new proxy connection succeed", clientId);
                break;
            case CONNECTION_REFUSED_NOT_AUTHORIZED:
                logger.logPxyConnWarn(log, INIT, "init new proxy connection failed due to proxy connections reached limit", clientId);
                break;
            case CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD:
            case CONNECTION_REFUSED_SERVER_UNAVAILABLE:
            default:
                logger.logPxyConnWarn(log, INIT, "init new proxy connection failed due to " + result.returnCode().toString(), clientId);
                break;
        }
        return proxySession;
    }

    /**
     * 生成用于建立代理连接的clientID
     *
     * @return clientID
     */
    private static String genClientId() {
        if (ControlSessionManager.config == null) {
            throw new IllegalStateException("control session was not initialized");
        }
        return ControlSessionManager.config.getInstanceName() + "/" + UUID.randomUUID().toString();
    }
}
