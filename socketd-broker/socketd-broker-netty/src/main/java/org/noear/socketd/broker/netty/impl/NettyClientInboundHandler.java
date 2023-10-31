package org.noear.socketd.broker.netty.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.noear.socketd.broker.netty.TcpNioClient;
import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.Flag;
import org.noear.socketd.protocol.Frame;
import org.noear.socketd.protocol.impl.ChannelDefault;

import java.util.concurrent.CompletableFuture;

public class NettyClientInboundHandler extends SimpleChannelInboundHandler<Frame> {
    private static AttributeKey<Channel> CHANNEL_KEY = AttributeKey.valueOf("CHANNEL_KEY");

    private final TcpNioClient client;
    private final CompletableFuture<Channel> channelFuture = new CompletableFuture<>();
    private Channel channel;

    public NettyClientInboundHandler(TcpNioClient client) {
        this.client = client;
    }

    public CompletableFuture<Channel> getChannel() {
        return channelFuture;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        channel = new ChannelDefault<>(ctx.channel(), client.config().getMaxRequests(), client.assistant());
        ctx.attr(CHANNEL_KEY).set(channel);

        //开始握手
        channel.sendConnect(client.config().getUrl());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Frame frame) throws Exception {
        Channel channel = ctx.attr(CHANNEL_KEY).get();
        client.processor().onReceive(channel, frame);

        if (frame.getFlag() == Flag.Connack) {
            channelFuture.complete(channel);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Channel channel = ctx.attr(CHANNEL_KEY).get();
        client.processor().onClose(channel.getSession());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.attr(CHANNEL_KEY).get();
        client.processor().onError(channel.getSession(), cause);
        ctx.close();
    }
}