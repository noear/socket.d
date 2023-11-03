package org.noear.socketd.transport.netty.tcp.impl;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.noear.socketd.transport.netty.tcp.TcpNioServer;
import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.impl.ChannelDefault;

/**
 * @author noear
 * @since 2.0
 */
@ChannelHandler.Sharable
public class NettyServerInboundHandler extends SimpleChannelInboundHandler<Frame> {
    private static AttributeKey<Channel> CHANNEL_KEY = AttributeKey.valueOf("CHANNEL_KEY");

    private TcpNioServer server;

    public NettyServerInboundHandler(TcpNioServer server) {
        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        Channel channel = new ChannelDefault<>(ctx.channel(), server.config(), server.assistant());
        ctx.attr(CHANNEL_KEY).set(channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Frame frame) throws Exception {
        Channel channel = ctx.attr(CHANNEL_KEY).get();
        server.processor().onReceive(channel, frame);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        Channel channel = ctx.attr(CHANNEL_KEY).get();
        server.processor().onClose(channel.getSession());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.attr(CHANNEL_KEY).get();
        server.processor().onError(channel.getSession(), cause);

        ctx.close();
    }
}