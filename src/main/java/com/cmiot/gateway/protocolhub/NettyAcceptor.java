package com.cmiot.gateway.protocolhub;

import com.cmiot.gateway.config.ConfigConsts;
import com.cmiot.gateway.config.IGatewayConfig;
import com.cmiot.gateway.custom.CustomProtocolDecoder;
import com.cmiot.gateway.custom.CustomProtocolEncoder;
import com.cmiot.gateway.exceptions.IllegalConfigException;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.mqttadaptor.handler.UpLinkHandler;
import com.cmiot.gateway.protocolhub.handler.DownLinkChannelHandler;
import com.cmiot.gateway.protocolhub.handler.ProtocolHubHandler;
import com.cmiot.gateway.protocolhub.handler.TcpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.INIT;
import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.LAUNCH;
import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

/**
 * 用于协议接入中心的初始化
 */
@Component
@Slf4j
final class NettyAcceptor {

    private Set<String> transportTypeSet = new HashSet<>();
    private Set<Integer> transportPortSet = new HashSet<>();
    private List<EventLoopGroup> eventLoopGroups = new ArrayList<>();

    private final ILogger logger;
    private final IGatewayConfig config;

    NettyAcceptor(IGatewayConfig iGatewayConfig, ILogger logger) {
        this.config = iGatewayConfig;
        this.logger = logger;
    }

    /**
     * 初始化
     */
    void initialize() {
        List<String> transport = config.getTransport();
        if (transport == null) {
            // 默认TCP 端口10086
            transport = Stream.of(ConfigConsts.DEFAULT_TRANSPORT).collect(Collectors.toList());
        }
        transport.forEach(s -> {
            String[] split = s.trim().split(":");
            if (split.length != 2) {
                throw new IllegalConfigException("illegal transport config: " + s);
            }
            String transportType = split[0].trim().toLowerCase();
            int transportPort;
            try {
                transportPort = Integer.parseInt(split[1].trim());
            } catch (Exception e) {
                throw new IllegalConfigException("illegal transport config: " + s);
            }

            if (!transportTypeSet.add(transportType)) {
                throw new IllegalConfigException("duplicate transport type: " + transportType);
            }
            if (!transportPortSet.add(transportPort)) {
                throw new IllegalConfigException("duplicate transport port: " + transportPort);
            }
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    initializeProtocolHub(transportType, transportPort);
                } catch (Exception e) {
                    logger.logProtocolHubError(log, INIT, "failed, type:" + transportType, e);
                    System.exit(1);
                }
            });
        });
    }


    /**
     * 初始化protocol hub
     *
     * @param type 协议类型
     * @param port 协议端口
     * @throws InterruptedException exception
     */
    private void initializeProtocolHub(String type, int port) throws InterruptedException {
        if ("tcp".equals(type)) {
            initializeTcpHub(port);
        } else {
            throw new IllegalConfigException("unsupported protocol type: " + type);
        }
    }

    /**
     * 初始化TCP协议接入中心
     *
     * @param port 端口
     * @throws InterruptedException exception
     */
    private void initializeTcpHub(int port) throws InterruptedException {
        ServerBootstrap bootstrap = configureTcpServerBootstrap();
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("idleStateHandler", new IdleStateHandler(60,
                        0, 0));
                pipeline.addLast("byteArrayDecoder", new ByteArrayDecoder());
                pipeline.addLast("byteArrayEncoder", new ByteArrayEncoder());
                pipeline.addLast("tcpHandler", new TcpServerHandler());
                pipeline.addLast("protocolHandler", new ProtocolHubHandler(
                        new CustomProtocolEncoder(),
                        new CustomProtocolDecoder(),
                        new UpLinkHandler(),
                        new DownLinkChannelHandler()));
            }
        });
        ChannelFuture f = bootstrap.bind(port);
        f.sync().addListener(FIRE_EXCEPTION_ON_FAILURE);
        logger.logInnerInfo(log, LAUNCH, "initialize tcp hub success, bind:" + port);
    }

    /**
     * 配置基于TCP的ServerBootstrap
     *
     * @return ServerBootstrap
     */
    private ServerBootstrap configureTcpServerBootstrap() {
        EventLoopGroup bossGroup;
        EventLoopGroup workerGroup;
        Class<? extends ServerSocketChannel> channelClass;
        if (Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup();
            workerGroup = new EpollEventLoopGroup();
            channelClass = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            channelClass = NioServerSocketChannel.class;
        }
        eventLoopGroups.add(bossGroup);
        eventLoopGroups.add(workerGroup);
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(channelClass)
                .option(ChannelOption.SO_BACKLOG, 1000000)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.AUTO_READ, false);
        return b;
    }

    /**
     * 关闭EventLoop
     */
    void close() {
        for (EventLoopGroup group : eventLoopGroups) {
            if (group == null) {
                throw new IllegalStateException("Invoked close on an Acceptor that wasn't initialized");
            }
            Future<?> waiter = group.shutdownGracefully();

            try {
                waiter.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException iex) {
            }

            if (!group.isTerminated()) {
                group.shutdownGracefully(0L, 0L, TimeUnit.MILLISECONDS);
            }

        }
    }
}
