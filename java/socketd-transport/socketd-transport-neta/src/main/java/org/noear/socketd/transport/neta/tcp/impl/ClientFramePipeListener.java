package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.SoChannel;
import net.hasor.neta.handler.PipeListener;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.*;

import java.util.concurrent.CompletableFuture;

/**
 * @author noear
 * @since 2.3
 */
public class ClientFramePipeListener implements PipeListener<Frame> {
    private Processor processor;
    private CompletableFuture<ClientHandshakeResult> handshakeFuture = new CompletableFuture<>();

    public ClientFramePipeListener(ChannelSupporter<NetChannel> supporter) {
        this.processor = supporter.getProcessor();
    }

    public CompletableFuture<ClientHandshakeResult> getHandshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void onReceive(SoChannel<?> soChannel, Frame frame) {
        ChannelInternal channel = (ChannelInternal) soChannel.getAttribute(Constants.ATT_KEY_CHANNEL);

        if (frame.flag() == Flags.Connack) {
            channel.onOpenFuture((r, e) -> {
                handshakeFuture.complete(new ClientHandshakeResult(channel, e));
            });
        }

        processor.onReceive(channel, frame);
    }

    @Override
    public void onError(SoChannel<?> soChannel, Throwable e, boolean isRcv) {
        ChannelInternal channel = (ChannelInternal) soChannel.getAttribute(Constants.ATT_KEY_CHANNEL);
        processor.onError(channel, e);
    }
}
