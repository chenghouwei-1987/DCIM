package com.cmiot.gateway.mqttadaptor;

import com.cmiot.gateway.config.IGatewayConfig;
import com.cmiot.gateway.exceptions.IllegalConfigException;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.mqttadaptor.handler.ProtocolMessageHandler;
import com.cmiot.gateway.mqttadaptor.mqtt.MqttSubscription;
import com.cmiot.gateway.mqttadaptor.mqtt.promise.MqttConnectResult;
import com.cmiot.gateway.utils.ExtensionUtils;
import com.cmiot.gateway.utils.SasTokenGenerator;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.INIT;
import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.LAUNCH;


/**
 * MQTT适配服务
 */
@Component
@Slf4j
public class MqttAdaptorService {

    private static ILogger logger = ExtensionUtils.getLogger();
    private final IGatewayConfig config;
    private final ProtocolMessageHandler protocolMessageHandler;

    private String instanceName;
    private String serviceId;

    public MqttAdaptorService(IGatewayConfig config, ProtocolMessageHandler protocolMessageHandler) {
        this.config = config;
        this.instanceName = config.getInstanceName();
        this.serviceId = config.getServiceId();
        this.protocolMessageHandler = protocolMessageHandler;
    }

    public void init() {
        startService();
    }

    private void startService() {
        try {
            logger.logInnerInfo(log, LAUNCH, serviceId, instanceName, "initialize control session");
            Channel channel = initControlConnection(config, protocolMessageHandler);
            ControlSessionManager.initControlSession(config, channel, protocolMessageHandler);
            logger.logInnerInfo(log, LAUNCH, serviceId, instanceName, "initialize control session succeed");
        } catch (Exception e) {
            logger.logCtrlConnError(log, INIT, serviceId, instanceName, "failed", e);
            System.exit(1);
        }
    }

    /**
     * 初始化控制连接
     *
     * @return 控制连接channel
     * @throws Exception exception
     */
    public static Channel initControlConnection(IGatewayConfig config, ProtocolMessageHandler protocolMessageHandler) throws Exception {
        // 实例启动时建立控制连接
        if (StringUtils.isEmpty(config.getInstanceName())) {
            throw new IllegalConfigException("config \"instanceName\" must be present");
        }
        MqttClient mqttClient = new MqttClient(config, protocolMessageHandler);

        String sasToken = SasTokenGenerator.gatewaySasToken();
        String serviceId = config.getServiceId();
        String instanceName = config.getInstanceName();
        MqttConnectResult result = mqttClient.connect(instanceName, serviceId, sasToken);
        if (result.returnCode() != MqttConnectReturnCode.CONNECTION_ACCEPTED) {
            logger.logCtrlConnError(log, INIT, serviceId, instanceName, "failed, error: " + result.returnCode().toString(), null);
            System.exit(1);
        }
        //订阅网关命令相关topic
        List<MqttSubscription> subscriptionList = new ArrayList<>();
        subscriptionList.add(new MqttSubscription(MqttQoS.AT_MOST_ONCE,
                String.format("$gw-ctrl/%s/%s/cmd/response/#", serviceId, instanceName)));
        subscriptionList.add(new MqttSubscription(MqttQoS.AT_MOST_ONCE,
                String.format("$gw-ctrl/%s/%s/cmd/request/#", serviceId, instanceName)));
        mqttClient.subscribe(subscriptionList);
        return mqttClient.getChannel();
    }
}
