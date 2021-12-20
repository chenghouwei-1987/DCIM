package com.cmiot.gateway.protocolhub;


import com.cmiot.gateway.extensions.logging.ILogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.INIT;


/**
 * 协议接入中心服务
 */
@Component
@Slf4j
public final class ProtocolHubService {

    private final ILogger logger;
    private final NettyAcceptor acceptor;

    public ProtocolHubService(NettyAcceptor acceptor, ILogger logger) {
        this.acceptor = acceptor;
        this.logger = logger;
    }

    public void init() {
        startService();
    }

    private void startService() {
        try {
            acceptor.initialize();
        } catch (Exception e) {
            logger.logProtocolHubError(log, INIT, "failed", e);
            System.exit(1);
        }
    }

    public void stopService() {
        if (acceptor != null) {
            acceptor.close();
        }
    }
}
