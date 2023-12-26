package org.noear.socketd.transport.netty.udp.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.AttributeKey;
import org.noear.socketd.transport.client.ClientHandshakeResult;
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
    private final CompletableFuture<ClientHandshakeResult> handshakeFuture = new CompletableFuture<>();
    private ChannelInternal channel;

    public NettyClientInboundHandler(UdpNioClient client) {
        this.client = client;
    }

    public CompletableFuture<ClientHandshakeResult> getHandshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        DatagramTagert tagert = new DatagramTagert(ctx.channel(), null,true);
        channel = new ChannelDefault<>(tagert, client);
        ctx.attr(CHANNEL_KEY).set(channel);

        //开始握手
        channel.sendConnect(client.getConfig().getUrl());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        ChannelInternal channel = ctx.attr(CHANNEL_KEY).get();
        Frame frame = client.getAssistant().read(packet.content());

        if (frame != null) {
            if (frame.getFlag() == Flags.Connack) {
                channel.onOpenFuture((r, e) -> {
                    handshakeFuture.complete(new ClientHandshakeResult(channel, e));
                });
            }

            client.getProcessor().onReceive(channel, frame);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);

        ChannelInternal channel = ctx.attr(CHANNEL_KEY).get();
        client.getProcessor().onError(channel, cause);
    }
}