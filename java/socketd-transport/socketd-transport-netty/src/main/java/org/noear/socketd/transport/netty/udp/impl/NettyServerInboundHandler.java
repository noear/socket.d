package org.noear.socketd.transport.netty.udp.impl;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.AttributeKey;
import org.noear.socketd.transport.netty.udp.UdpNioServer;
import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.impl.ChannelDefault;

/**
 * @author noear
 * @since 2.0
 */
@ChannelHandler.Sharable
public class NettyServerInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static AttributeKey<Channel> CHANNEL_KEY = AttributeKey.valueOf("CHANNEL_KEY");

    private UdpNioServer server;

    public NettyServerInboundHandler(UdpNioServer server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        Channel channel = ctx.attr(CHANNEL_KEY).get();
        if (channel == null) {
            DatagramTagert tagert = new DatagramTagert(ctx.channel(), packet, false);
            channel = new ChannelDefault<>(tagert, server.config(), server.assistant());
            ctx.attr(CHANNEL_KEY).set(channel);
        }

        Frame frame = server.assistant().read(packet.content());

        if (frame != null) {
            server.processor().onReceive(channel, frame);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.attr(CHANNEL_KEY).get();
        server.processor().onError(channel.getSession(), cause);

        ctx.close();
    }
}