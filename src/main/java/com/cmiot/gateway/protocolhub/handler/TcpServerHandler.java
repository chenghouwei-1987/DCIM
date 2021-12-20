package com.cmiot.gateway.protocolhub.handler;

import com.cmiot.gateway.entity.ProtocolType;
import com.cmiot.gateway.utils.DeviceSessionNettyUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * TCP协议接入中心Handler
 */
public final class TcpServerHandler extends SimpleChannelInboundHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 绑定设备连接的远程IP地址和协议站类型
        DeviceSessionNettyUtils.setDeviceRemoteAddress(ctx.channel(), ctx.channel().remoteAddress().toString());
        DeviceSessionNettyUtils.setDeviceProtocolType(ctx.channel(), ProtocolType.TCP);
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        ctx.fireChannelRead(msg);
    }
}
