package com.cmiot.gateway.extensions.metric;

import com.cmiot.gateway.config.IGatewayConfig;
import com.cmiot.gateway.entity.DeviceSession;
import com.cmiot.gateway.entity.ProxySession;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.mqttadaptor.ProxySessionManager;
import com.cmiot.gateway.protocolhub.DevSessionManager;
import com.cmiot.gateway.utils.ExtensionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.StringJoiner;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.RUNTIME;

/**
 * Metric
 */
@Component
@Slf4j
public class Metric {

    private static final String UP_FLOW_EACH_CYCLE = "upFlowEachCycle";
    private static final String DOWN_FLOW_EACH_CYCLE = "downFlowEachCycle";
    private static final String CURRENT_CONNECTION_COUNTS = "currentConnCounts";
    private static final String CURRENT_DEVICE_COUNTS = "currentDevCounts";

    private ILogger logger = ExtensionUtils.getLogger();

    private String svcId;
    private String instName;

    public Metric() {
        IGatewayConfig config = ExtensionUtils.getGatewayConfig();
        svcId = config.getServiceId();
        instName = config.getInstanceName();
    }


    /**
     * 平台到网关的下行流量统计/周期
     */
    private AtomicInteger downFlowEachCycle = new AtomicInteger(0);

    /**
     * 网关到平台的上行流量统计/周期
     */
    private AtomicInteger upFlowEachCycle = new AtomicInteger(0);

    void incrUpFlow(int bytesNum) {
        upFlowEachCycle.addAndGet(bytesNum);
    }

    void incrDownFlow(int bytesNum) {
        downFlowEachCycle.addAndGet(bytesNum);
    }


    /**
     * 获取当前网关到平台的连接数量
     */
    private int getCurrentConnCounts() {
        ConcurrentMap<String, ProxySession> proxySessionPool = ProxySessionManager.getProxySessionPool();
        return proxySessionPool.size();
    }

    /**
     * 获取网关当前代理的设备数量
     */
    private int getCurrentDevCounts() {
        ConcurrentMap<String, DeviceSession> devSessionPool = DevSessionManager.getDevSessionPool();
        return devSessionPool.size();
    }


    @Scheduled(cron = "${metric.cron}")
    private void stat() {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(UP_FLOW_EACH_CYCLE + ":" + upFlowEachCycle.getAndSet(0));
        joiner.add(DOWN_FLOW_EACH_CYCLE + ":" + downFlowEachCycle.getAndSet(0));
        joiner.add(CURRENT_CONNECTION_COUNTS + ":" + getCurrentConnCounts());
        joiner.add(CURRENT_DEVICE_COUNTS + ":" + getCurrentDevCounts());
        logger.logMetricInfo(log, RUNTIME, svcId, instName, joiner.toString(), null);
    }
}
