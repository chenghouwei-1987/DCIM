package com.cmiot.gateway.mqttadaptor;

import com.cmiot.gateway.config.ConfigConsts;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.extensions.metric.MetricHandler;
import com.cmiot.gateway.mqttadaptor.handler.ContextSSLFactory;
import com.cmiot.gateway.mqttadaptor.handler.MqttHandler;
import com.cmiot.gateway.mqttadaptor.handler.NettySocketSslHandler;
import com.cmiot.gateway.mqttadaptor.handler.ProtocolMessageHandler;
import com.cmiot.gateway.mqttadaptor.mqtt.MqttPingHandler;
import com.cmiot.gateway.utils.ExtensionUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.util.StringUtils;

import java.net.URI;

/**
 * Mqtt Client工厂类
 */
final class MqttClientFactory {

    private ILogger logger = ExtensionUtils.getLogger();
    private static MetricHandler metricHandler = new MetricHandler(ExtensionUtils.getMetric());

    /**
     * 初始化Netty Client
     *
     * @param host       connection host地址
     * @param tlsSupport 是否支持tls加密
     * @param handler    内部数据handler
     * @return 连接channel
     */
    static Channel initializeNettyClient(String host, Boolean tlsSupport, ProtocolMessageHandler handler) {
        tlsSupport = tlsSupport == null ? ConfigConsts.DEFAULT_TLS_SUPPORT : tlsSupport;
        if (StringUtils.isEmpty(host)) {
            host = tlsSupport ? ConfigConsts.DEFAULT_TLS_CONNECTION_HOST : ConfigConsts.DEFAULT_CONNECTION_HOST;
        }
        URI broker = URI.create(host);
        EventLoopGroup eventLoopGroup;
        Class<? extends AbstractChannel> channelClass;
        if (Epoll.isAvailable()) {
            eventLoopGroup = new EpollEventLoopGroup();
            channelClass = EpollSocketChannel.class;
        } else {
            eventLoopGroup = new NioEventLoopGroup();
            channelClass = NioSocketChannel.class;
        }
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup);
        b.channel(channelClass);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.TCP_NODELAY, true);
        Boolean finalTlsSupport = tlsSupport;
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ChannelPipeline p = ch.pipeline();
                p.addLast("metricHandler", metricHandler);
                p.addLast("idleStateHandler", new IdleStateHandler(60, 60, 0));
                p.addLast("mqttDecoder", new MqttDecoder());
                p.addLast("mqttPingHandler", new MqttPingHandler(60));
                p.addLast("mqttEncoder", MqttEncoder.INSTANCE);
                p.addLast("mqttHandler", new MqttHandler());
                p.addLast("protocolMessageHandler", handler);

                if (finalTlsSupport) {
                    p.addLast("sslHandler", new NettySocketSslHandler());
                    p.addFirst("ssl", ContextSSLFactory.getSslContext().newHandler(ch.alloc()));
                }
            }
        });

        try {
            return b.connect(broker.getHost(), broker.getPort()).sync().channel();
        } catch (InterruptedException e) {
            System.exit(1);
            return null;
        }
    }
}
