package org.noear.socketd.transport.java_kcp.impl;

import io.netty.buffer.ByteBuf;
import kcp.KcpListener;
import kcp.Ukcp;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Flags;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.CodecReader;
import org.noear.socketd.transport.core.impl.ChannelDefault;
import org.noear.socketd.transport.java_kcp.KcpNioClient;

import java.util.concurrent.CompletableFuture;

/**
 * @author noear
 * @since 2.1
 */
public class ClientKcpListener implements KcpListener {
    private final KcpNioClient client;
    private final CompletableFuture<ClientHandshakeResult> handshakeFuture = new CompletableFuture<>();

    public CompletableFuture<ClientHandshakeResult> getHandshakeFuture() {
        return handshakeFuture;
    }

    public ClientKcpListener(KcpNioClient client){
        this.client = client;
    }

    @Override
    public void onConnected(Ukcp ukcp) {
        ChannelInternal channel = new ChannelDefault<>(ukcp, client);
        ukcp.user().setCache(channel);

        //开始握手
        try {
            channel.sendConnect(client.getConfig().getUrl(), client.getConfig().getMetaMap());
        } catch (Throwable e) {
            channel.onError(e);
        }
    }

    @Override
    public void handleReceive(ByteBuf byteBuf, Ukcp ukcp) {
        CodecReader reader = new NettyBufferCodecReader(byteBuf);
        Frame frame = client.getConfig().getCodec().read(reader);
        if (frame == null) {
            return;
        }

        ChannelInternal channel = ukcp.user().getCache();

        try {
            if (frame.flag() == Flags.Connack) {
                channel.onOpenFuture((r, e) -> {
                    handshakeFuture.complete(new ClientHandshakeResult(channel, e));
                });
            }

            client.getProcessor().reveFrame(channel, frame);
        } catch (Throwable e) {
            client.getProcessor().onError(channel, e);

            //说明握手失败了
            handshakeFuture.complete(new ClientHandshakeResult(channel, e));
        }
    }

    @Override
    public void handleException(Throwable throwable, Ukcp ukcp) {
        ChannelInternal channel = ukcp.user().getCache();
        client.getProcessor().onError(channel, throwable);
    }

    @Override
    public void handleClose(Ukcp ukcp) {
        ChannelInternal channel = ukcp.user().getCache();
        client.getProcessor().onClose(channel);
    }
}
