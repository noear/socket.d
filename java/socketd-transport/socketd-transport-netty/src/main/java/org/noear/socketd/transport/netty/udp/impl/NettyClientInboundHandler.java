package org.noear.socketd.transport.netty.udp.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.AttributeKey;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Flags;
import org.noear.socketd.transport.core.internal.ChannelDefault;
import org.noear.socketd.transport.netty.udp.UdpNioClient;
import org.noear.socketd.transport.core.Frame;

import java.util.concurrent.CompletableFuture;

/**
 * @author noear
 * @since 2.0
 */
public class NettyClientInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static AttributeKey<ChannelInternal> CHANNEL_KEY = AttributeKey.valueOf("CHANNEL_KEY");

    private final UdpNioClient client;
    private final CompletableFuture<ChannelInternal> channelFuture = new CompletableFuture<>();
    private ChannelInternal channel;

    public NettyClientInboundHandler(UdpNioClient client) {
        this.client = client;
    }

    public CompletableFuture<ChannelInternal> getChannel() {
        return channelFuture;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        DatagramTagert tagert = new DatagramTagert(ctx.channel(), null,true);
        channel = new ChannelDefault<>(tagert, client);
        ctx.attr(CHANNEL_KEY).set(channel);

        //开始握手
        channel.sendConnect(client.config().getUrl());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        ChannelInternal channel = ctx.attr(CHANNEL_KEY).get();
        Frame frame = client.assistant().read(packet.content());

        if (frame != null) {
            client.processor().onReceive(channel, frame);

            if (frame.getFlag() == Flags.Connack) {
                //握手完成，通道可用了
                channelFuture.complete(channel);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);

        ChannelInternal channel = ctx.attr(CHANNEL_KEY).get();
        client.processor().onError(channel, cause);
    }
}