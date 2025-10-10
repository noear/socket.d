package org.noear.socketd.transport.neta.listener;

import net.hasor.neta.channel.PlayLoad;
import net.hasor.neta.channel.PlayLoadListener;
import net.hasor.neta.channel.SoCloseException;
import net.hasor.neta.channel.SoTimeoutException;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.client.ClientInternal;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Flags;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.Processor;

import java.util.concurrent.CompletableFuture;

/**
 * @author noear
 * @since 2.3
 */
public class ClientListener implements PlayLoadListener {
    private final Processor                                processor;
    private final CompletableFuture<ClientHandshakeResult> handshakeFuture = new CompletableFuture<>();

    public ClientListener(ClientInternal supporter) {
        this.processor = supporter.getProcessor();
    }

    public CompletableFuture<ClientHandshakeResult> getHandshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void onEvent(PlayLoad data) {
        ChannelInternal channel = data.getSource().findProtoContext(ChannelInternal.class);
        if (data.isSuccess()) {
            this.onData(channel, (Frame) data.getData());
        } else {
            this.onError(channel, data.getError());
        }
    }

    private void onData(ChannelInternal channel, Frame frame) {
        if (frame.flag() == Flags.Connack) {
            channel.onOpenFuture((r, e) -> {
                handshakeFuture.complete(new ClientHandshakeResult(channel, e));
            });
        }
        processor.reveFrame(channel, frame);
    }

    public void onError(ChannelInternal channel, Throwable e) {
        if (e instanceof SoCloseException) {
            processor.onClose(channel);
        } else if (e instanceof SoTimeoutException) {
            processor.onError(channel, e);
        } else {
            processor.onError(channel, e);
        }
    }
}
