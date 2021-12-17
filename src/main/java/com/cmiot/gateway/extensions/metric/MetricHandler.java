package com.cmiot.gateway.extensions.metric;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.springframework.stereotype.Component;



/**
 * 出入流量监控处理Handler
 */
@Component
@ChannelHandler.Sharable
public class MetricHandler extends ChannelDuplexHandler {
    private Metric metric;

    public MetricHandler(Metric metric) {
        this.metric = metric;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        metric.incrDownFlow(byteBuf.readableBytes());
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        metric.incrUpFlow(byteBuf.readableBytes());
        ctx.write(msg, promise);
    }
}
