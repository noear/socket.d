package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.ProtoContext;
import net.hasor.neta.channel.SoCloseException;
import net.hasor.neta.channel.SoTimeoutException;
import net.hasor.neta.handler.*;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.Processor;

import java.util.Objects;

/**
 * @author noear
 * @since 2.3
 */
public class ServerPipeListener implements ProtoHandler<Frame, Frame> {
    private final Processor processor;

    public ServerPipeListener(ChannelSupporter<NetChannel> supporter) {
        this.processor = supporter.getProcessor();
    }

    @Override
    public ProtoStatus onMessage(ProtoContext context, ProtoRcvQueue<Frame> src, ProtoSndQueue<Frame> dst) throws Throwable {
        ChannelInternal channel = context.context(ChannelInternal.class);

        while (src.hasMore()) {
            processor.reveFrame(channel, src.takeMessage());
        }

        return ProtoStatus.Next;
    }

    @Override
    public ProtoStatus onError(ProtoContext context, Throwable e, ProtoExceptionHolder eh) throws Throwable {
        ChannelInternal channel = Objects.requireNonNull(context.context(ChannelInternal.class));

        if (e instanceof SoCloseException) {
            processor.onClose(channel);
        } else if (e instanceof SoTimeoutException) {
            processor.onError(channel, e);
        } else {
            processor.onError(channel, e);
        }

        return ProtoStatus.Next;
    }
}
