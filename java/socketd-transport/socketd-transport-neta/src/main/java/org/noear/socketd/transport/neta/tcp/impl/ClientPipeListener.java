package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.ProtoContext;
import net.hasor.neta.channel.SoCloseException;
import net.hasor.neta.channel.SoTimeoutException;
import net.hasor.neta.handler.*;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.*;

import java.util.concurrent.CompletableFuture;

/**
 * @author noear
 * @since 2.3
 */
public class ClientPipeListener implements ProtoHandler<Frame, Frame> {
    private final Processor                                processor;
    private final CompletableFuture<ClientHandshakeResult> handshakeFuture = new CompletableFuture<>();

    public ClientPipeListener(ChannelSupporter<NetChannel> supporter) {
        this.processor = supporter.getProcessor();
    }

    public CompletableFuture<ClientHandshakeResult> getHandshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public ProtoStatus onMessage(ProtoContext context, ProtoRcvQueue<Frame> src, ProtoSndQueue<Frame> dst) throws Throwable {
        ChannelInternal channel = context.context(ChannelInternal.class);

        while (src.hasMore()) {
            Frame frame = src.takeMessage();
            if (frame.flag() == Flags.Connack) {
                channel.onOpenFuture((r, e) -> {
                    handshakeFuture.complete(new ClientHandshakeResult(channel, e));
                });
            }

            processor.reveFrame(channel, frame);
        }
        return ProtoStatus.Next;
    }

    @Override
    public ProtoStatus onError(ProtoContext context, Throwable e, ProtoExceptionHolder eh) throws Throwable {
        ChannelInternal channel = context.context(ChannelInternal.class);

        if (channel != null) {
            //todo:有出现过 channel 为 null 的情况！（说明激活事件，没有100%触发）
            if (e instanceof SoCloseException) {
                processor.onClose(channel);
            } else if (e instanceof SoTimeoutException) {
                processor.onError(channel, e);
            } else {
                processor.onError(channel, e);
            }
        }

        return ProtoStatus.Next;
    }
}
