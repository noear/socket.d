package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.SoChannel;
import net.hasor.neta.handler.PipeListener;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.Processor;

/**
 * @author noear
 * @since 2.3
 */
public class FramePipeListener implements PipeListener<Frame> {
    private Processor processor;

    public FramePipeListener(ChannelSupporter<NetChannel> supporter) {
        this.processor = supporter.getProcessor();
    }

    @Override
    public void onReceive(SoChannel<?> soChannel, Frame frame) {
        ChannelInternal channel = (ChannelInternal) soChannel.getAttribute("SESSION_KEY");
        processor.onReceive(channel, frame);
    }

    @Override
    public void onError(SoChannel<?> soChannel, Throwable e, boolean isRcv) {
        ChannelInternal channel = (ChannelInternal) soChannel.getAttribute("SESSION_KEY");
        processor.onError(channel, e);
    }
}
