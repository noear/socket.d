package org.noear.socketd.broker.netty.impl;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.noear.socketd.broker.netty.TcpNioServer;
import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.Frame;
import org.noear.socketd.protocol.impl.ChannelDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class NettyServerProcessor extends SimpleChannelInboundHandler<Frame> {
    private static final Logger log = LoggerFactory.getLogger(NettyServerProcessor.class);
    private static AttributeKey<Channel> CHANNEL_KEY = AttributeKey.valueOf("CHANNEL_KEY");

    private TcpNioServer server;

    public NettyServerProcessor(TcpNioServer server) {
        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        Channel channel = new ChannelDefault<>(ctx.channel(), ctx.channel()::close, server.exchanger());
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