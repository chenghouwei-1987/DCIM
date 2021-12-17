package com.cmiot.gateway.protocolhub.handler;

import com.cmiot.gateway.custom.CustomProtocolDecoder;
import com.cmiot.gateway.custom.CustomProtocolEncoder;
import com.cmiot.gateway.entity.DeviceMessage;
import com.cmiot.gateway.entity.DeviceSession;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.mqttadaptor.handler.UpLinkHandler;
import com.cmiot.gateway.protocolhub.DevSessionManager;
import com.cmiot.gateway.utils.DeviceSessionNettyUtils;
import com.cmiot.gateway.utils.ExtensionUtils;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.RUNTIME;

/**
 * 协议接入中心核心Handler，连接自定义数据的编解码、设备消息的接收和发送
 */
@Slf4j
public final class ProtocolHubHandler extends ChannelDuplexHandler {

    private ILogger logger = ExtensionUtils.getLogger();

    private CustomProtocolEncoder encoder;
    private CustomProtocolDecoder decoder;
    private UpLinkHandler upLinkHandler;
    private DownLinkChannelHandler downLinkChannelHandler;

    public ProtocolHubHandler(CustomProtocolEncoder encoder, CustomProtocolDecoder decoder, UpLinkHandler upLinkHandler, DownLinkChannelHandler downLinkChannelHandler) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.upLinkHandler = upLinkHandler;
        this.downLinkChannelHandler = downLinkChannelHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.logInnerInfo(log, RUNTIME, ctx.channel().remoteAddress().toString());
        ctx.channel().read();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        DeviceMessage deviceMessage = (DeviceMessage) msg;
        Object data = encoder.encode(deviceMessage);
        if (data == null) {
            return;
        }
        ctx.writeAndFlush(data);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 解码
        DeviceMessage deviceMessage = decoder.decode((byte[]) msg);
        if (deviceMessage != null) {
            DeviceSession devSession = DevSessionManager.getDevSession(deviceMessage.getDeviceId());
            if (devSession == null) {
                // 创建DeviceSession
                devSession = DevSessionManager.createDevSession(deviceMessage.getProductId(),
                        deviceMessage.getDeviceId(),
                        deviceMessage.getDeviceName(),
                        ctx.channel());
                DevSessionManager.putDevSession(devSession);
                DeviceSessionNettyUtils.setDeviceSession(ctx.channel(), devSession);
            }
            // 设备上行数据处理
            upLinkHandler.handleMessage(deviceMessage);
        }
        ctx.channel().read();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        DeviceSession devSession = DeviceSessionNettyUtils.deviceSession(ctx.channel());
        DevSessionManager.handleConnectionLost(devSession);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!(cause instanceof IOException)) {
            String reason = cause.getLocalizedMessage();
            DeviceSessionNettyUtils.setDeviceCloseReason(ctx.channel(), reason);
        } else {
            logger.logInnerError(log, RUNTIME, "exceptionCaught", cause);
        }
        if (ctx.channel().isActive()) {
            ctx.close();
        }
    }
}
