package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.SoChannel;
import net.hasor.neta.handler.PipeListener;
import org.noear.socketd.transport.core.*;

/**
 * @author noear
 * @since 2.3
 */
public class ServerFramePipeListener implements PipeListener<Frame> {
    private Processor processor;

    public ServerFramePipeListener(ChannelSupporter<NetChannel> supporter) {
        this.processor = supporter.getProcessor();
    }

    @Override
    public void onReceive(SoChannel<?> soChannel, Frame frame) {
        ChannelInternal channel = (ChannelInternal) soChannel.getAttribute(Constants.ATT_KEY_CHANNEL);
        processor.onReceive(channel, frame);
    }

    @Override
    public void onError(SoChannel<?> soChannel, Throwable e, boolean isRcv) {
        ChannelInternal channel = (ChannelInternal) soChannel.getAttribute(Constants.ATT_KEY_CHANNEL);
        processor.onError(channel, e);
    }
}
