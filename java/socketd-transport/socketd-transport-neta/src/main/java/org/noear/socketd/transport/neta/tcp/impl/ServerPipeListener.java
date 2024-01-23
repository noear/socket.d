package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.SoChannel;
import net.hasor.neta.channel.SoCloseException;
import net.hasor.neta.channel.SoTimeoutException;
import net.hasor.neta.handler.PipeListener;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.Processor;

/**
 * @author noear
 * @since 2.3
 */
public class ServerPipeListener implements PipeListener<Frame> {
    private final Processor processor;

    public ServerPipeListener(ChannelSupporter<NetChannel> supporter) {
        this.processor = supporter.getProcessor();
    }

    @Override
    public void onReceive(SoChannel<?> soChannel, Frame frame) {
        ChannelInternal channel = soChannel.findPipeContext(ChannelInternal.class);
        processor.onReceive(channel, frame);
    }

    @Override
    public void onError(SoChannel<?> soChannel, Throwable e, boolean isRcv) {
        ChannelInternal channel = soChannel.findPipeContext(ChannelInternal.class);

        if (e instanceof SoCloseException) {
            processor.onClose(channel);
        } else if (e instanceof SoTimeoutException) {
            processor.onError(channel, e);
        } else {
            processor.onError(channel, e);
        }
    }
}
