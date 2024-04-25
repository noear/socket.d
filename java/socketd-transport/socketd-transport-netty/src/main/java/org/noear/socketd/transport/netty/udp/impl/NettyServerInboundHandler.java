package org.noear.socketd.transport.netty.udp.impl;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.AttributeKey;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Flags;
import org.noear.socketd.transport.core.impl.ChannelDefault;
import org.noear.socketd.transport.netty.udp.UdpNioServer;
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

    protected ChannelInternal getChannel(ChannelHandlerContext ctx, DatagramPacket packet, boolean isNewConnect){
        ChannelInternal channel0 = ctx.attr(CHANNEL_KEY).get();

        if (isNewConnect) {
            if (channel0 != null) {
                //如果是新连接，并且有旧的通道；先把旧的关闭
                try {
                    server.getProcessor().onClose(channel0);
                } catch (Throwable e) {
                    server.getProcessor().onError(channel0, e);
                }
                channel0 = null;
            }
        }


        if (channel0 == null) {
            DatagramTagert tagert = new DatagramTagert(ctx, packet, false);
            channel0 = new ChannelDefault<>(tagert, server);
            ctx.attr(CHANNEL_KEY).set(channel0);
        }

        return channel0;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        Frame frame = server.getAssistant().read(packet.content());

        if(frame == null){
            return;
        }

        boolean isNewConnect = frame.flag() == Flags.Connect;
        ChannelInternal channel = getChannel(ctx, packet, isNewConnect);

        server.getProcessor().onReceive(channel, frame);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);

        ChannelInternal channel = ctx.attr(CHANNEL_KEY).get();
        server.getProcessor().onError(channel, cause);
    }
}