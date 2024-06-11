package org.noear.socketd.transport.netty.tcp.impl;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.netty.tcp.TcpNioServer;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.impl.ChannelDefault;

/**
 * @author noear
 * @since 2.0
 */
@ChannelHandler.Sharable
public class NettyServerInboundHandler extends SimpleChannelInboundHandler<Frame> {
    private static AttributeKey<ChannelInternal> CHANNEL_KEY = AttributeKey.valueOf("CHANNEL_KEY");

    private final TcpNioServer server;

    public NettyServerInboundHandler(TcpNioServer server) {
        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        ChannelInternal channel = new ChannelDefault<>(ctx.channel(), server);
        ctx.attr(CHANNEL_KEY).set(channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Frame frame) throws Exception {
        ChannelInternal channel = ctx.attr(CHANNEL_KEY).get();
        server.getProcessor().reveFrame(channel, frame);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        ChannelInternal channel = ctx.attr(CHANNEL_KEY).get();
        server.getProcessor().onClose(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ChannelInternal channel = ctx.attr(CHANNEL_KEY).get();
        server.getProcessor().onError(channel, cause);

        ctx.close();
    }
}