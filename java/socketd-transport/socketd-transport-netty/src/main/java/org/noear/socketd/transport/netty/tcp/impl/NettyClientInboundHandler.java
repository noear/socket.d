package org.noear.socketd.transport.netty.tcp.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.noear.socketd.exception.SocketDConnectionException;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.netty.tcp.TcpNioClient;
import org.noear.socketd.transport.core.Flags;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.impl.ChannelDefault;

import java.util.concurrent.CompletableFuture;

/**
 * @author noear
 * @since 2.0
 */
public class NettyClientInboundHandler extends SimpleChannelInboundHandler<Frame> {
    private static AttributeKey<ChannelInternal> CHANNEL_KEY = AttributeKey.valueOf("CHANNEL_KEY");

    private final TcpNioClient client;
    private final CompletableFuture<ClientHandshakeResult> handshakeFuture = new CompletableFuture<>();


    public NettyClientInboundHandler(TcpNioClient client) {
        this.client = client;
    }

    public CompletableFuture<ClientHandshakeResult> getHandshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        ChannelInternal channel = new ChannelDefault<>(ctx.channel(), client);
        ctx.attr(CHANNEL_KEY).set(channel);

        //开始握手
        channel.sendConnect(client.getConfig().getUrl(), client.getConfig().getMetaMap());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Frame frame) throws Exception {
        ChannelInternal channel = ctx.attr(CHANNEL_KEY).get();

        try {
            if (frame.flag() == Flags.Connack) {
                channel.onOpenFuture((r, e) -> {
                    handshakeFuture.complete(new ClientHandshakeResult(channel, e));
                });
            }

            client.getProcessor().onReceive(channel, frame);
        } catch (SocketDConnectionException e) {
            //说明握手失败了
            handshakeFuture.complete(new ClientHandshakeResult(channel, e));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        ChannelInternal channel = ctx.attr(CHANNEL_KEY).get();
        client.getProcessor().onClose(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ChannelInternal channel = ctx.attr(CHANNEL_KEY).get();
        client.getProcessor().onError(channel, cause);
        ctx.close();
    }
}