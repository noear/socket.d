package org.noear.socketd.transport.netty.udp.impl;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.AttributeKey;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.internal.ChannelDefault;
import org.noear.socketd.transport.netty.udp.UdpNioServer;
import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.Frame;

/**
 * @author noear
 * @since 2.0
 */
@ChannelHandler.Sharable
public class NettyServerInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static AttributeKey<ChannelInternal> CHANNEL_KEY = AttributeKey.valueOf("CHANNEL_KEY");

    private UdpNioServer server;

    public NettyServerInboundHandler(UdpNioServer server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        ChannelInternal channel = ctx.attr(CHANNEL_KEY).get();
        if (channel == null) {
            DatagramTagert tagert = new DatagramTagert(ctx.channel(), packet, false);
            channel = new ChannelDefault<>(tagert, server);
            ctx.attr(CHANNEL_KEY).set(channel);
        }

        Frame frame = server.getAssistant().read(packet.content());

        if (frame != null) {
            server.getProcessor().onReceive(channel, frame);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);

        Channel channel = ctx.attr(CHANNEL_KEY).get();
        server.getProcessor().onError(channel, cause);
    }
}