package com.cmiot.gateway.mqttadaptor.handler;

import com.cmiot.gateway.exceptions.InternalException;
import com.cmiot.gateway.extensions.logging.ILogger;
import com.cmiot.gateway.utils.ExtensionUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.RUNTIME;

/**
 * TLS加密Handler
 */
@Slf4j
public class NettySocketSslHandler extends SimpleChannelInboundHandler<ByteBuffer> {

    private ILogger logger = ExtensionUtils.getLogger();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        // Once session is secured, send a greeting and register the channel to the global channel
        // list so the channel received the messages from others.
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                (GenericFutureListener<Future<Channel>>) future -> {
                    if (future.isSuccess()) {
                        byte[] array = new byte[]{(byte) 7d, 04};
                        ByteBuffer bu = ByteBuffer.wrap(array);
                        ctx.channel().writeAndFlush(bu);
                    } else {
                        logger.logInnerError(log, RUNTIME, "Handshake failed, is the certificate correct?", null);
                        throw new InternalException("Handshake failed");
                    }
                    ctx.writeAndFlush(
                            "Welcome to " + InetAddress.getLocalHost().getHostName() +
                                    " secure chat service!\n");
                    ctx.writeAndFlush(
                            "Your session is protected by " +
                                    ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() +
                                    " cipher suite.\n");

                });
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.logInnerError(log, RUNTIME, "Unexpected exception from downstream. cause: ", cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuffer msg) throws Exception {
        byte[] array = new byte[]{00, 01, 00, 00, 00, 06, 05, 03, (byte) 7d, 00, 00, 07};
        ByteBuffer bu = ByteBuffer.wrap(array);
        ctx.channel().writeAndFlush(bu);
    }
}
