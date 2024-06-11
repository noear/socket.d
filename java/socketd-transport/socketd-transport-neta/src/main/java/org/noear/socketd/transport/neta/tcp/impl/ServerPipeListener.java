package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.PipeContext;
import net.hasor.neta.channel.SoCloseException;
import net.hasor.neta.channel.SoTimeoutException;
import net.hasor.neta.handler.*;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.Processor;

/**
 * @author noear
 * @since 2.3
 */
public class ServerPipeListener implements PipeHandler<Frame, Frame> {
    private final Processor processor;

    public ServerPipeListener(ChannelSupporter<NetChannel> supporter) {
        this.processor = supporter.getProcessor();
    }

    @Override
    public PipeStatus onMessage(PipeContext context, PipeRcvQueue<Frame> src, PipeSndQueue<Frame> dst) throws Throwable {
        ChannelInternal channel = context.context(ChannelInternal.class);

        while (src.hasMore()) {
            processor.reveFrame(channel, src.takeMessage());
        }

        return PipeStatus.Next;
    }

    @Override
    public PipeStatus onError(PipeContext context, Throwable e, PipeExceptionHolder eh) throws Throwable {
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

        return PipeStatus.Next;
    }
}
