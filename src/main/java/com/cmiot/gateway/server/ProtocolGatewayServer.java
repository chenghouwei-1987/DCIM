package com.cmiot.gateway.server;

import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.mqttadaptor.MqttAdaptorService;
import com.cmiot.gateway.protocolhub.ProtocolHubService;
import com.cmiot.gateway.utils.ExtensionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.*;

/**
 * 协议网关服务启动类
 */
@Component
@Slf4j
public final class ProtocolGatewayServer {

    private ILogger logger = ExtensionUtils.getLogger();

    private final ProtocolHubService protocolHubService;

    private final MqttAdaptorService mqttAdaptorService;

    public ProtocolGatewayServer(ProtocolHubService protocolHubService, MqttAdaptorService mqttAdaptorService) {
        this.protocolHubService = protocolHubService;
        this.mqttAdaptorService = mqttAdaptorService;
    }

    @PostConstruct
    public void initServer() {
        logger.logInnerInfo(log, LAUNCH, "initialize protocol gateway server");
        try {
            protocolHubService.init();
            mqttAdaptorService.init();
        } catch (Exception e) {
            logger.logInnerError(log, INIT, "failed", e);
        }
    }

    @PreDestroy
    public void stopServer() {
        logger.logInnerInfo(log, SHUTDOWN, "stop protocol gateway server.");
        protocolHubService.stopService();
        logger.logInnerInfo(log, SHUTDOWN, "stop protocol gateway server finished.");
    }
}
