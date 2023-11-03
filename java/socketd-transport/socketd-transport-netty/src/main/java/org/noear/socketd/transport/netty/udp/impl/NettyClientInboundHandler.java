package org.noear.socketd.transport.netty.udp.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.AttributeKey;
import org.noear.socketd.transport.netty.udp.UdpNioClient;
import org.noear.socketd.core.Channel;
import org.noear.socketd.core.Flag;
import org.noear.socketd.core.Frame;
import org.noear.socketd.core.impl.ChannelDefault;

import java.util.concurrent.CompletableFuture;

/**
 * @author noear
 * @since 2.0
 */
public class NettyClientInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static AttributeKey<Channel> CHANNEL_KEY = AttributeKey.valueOf("CHANNEL_KEY");

    private final UdpNioClient client;
    private final CompletableFuture<Channel> channelFuture = new CompletableFuture<>();
    private Channel channel;

    public NettyClientInboundHandler(UdpNioClient client) {
        this.client = client;
    }

    public CompletableFuture<Channel> getChannel() {
        return channelFuture;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        DatagramTagert tagert = new DatagramTagert(ctx.channel(), null,true);
        channel = new ChannelDefault<>(tagert, client.config(), client.assistant());
        ctx.attr(CHANNEL_KEY).set(channel);

        //开始握手
        channel.sendConnect(client.config().getUrl());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        Channel channel = ctx.attr(CHANNEL_KEY).get();
        Frame frame = client.assistant().read(packet.content());

        if (frame != null) {
            client.processor().onReceive(channel, frame);

            if (frame.getFlag() == Flag.Connack) {
                //握手完成，通道可用了
                channelFuture.complete(channel);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);

        Channel channel = ctx.attr(CHANNEL_KEY).get();
        client.processor().onError(channel.getSession(), cause);
        ctx.close();
    }
}