package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.SoChannel;
import net.hasor.neta.handler.PipeListener;
import org.noear.socketd.transport.core.Frame;

/**
 * @author noear
 * @since 2.3
 */
public class FramePipeListener implements PipeListener<Frame> {
    @Override
    public void onReceive(SoChannel<?> soChannel, Frame frame) {
        NetChannel netChannel = ((NetChannel) soChannel);
    }

    @Override
    public void onError(SoChannel<?> channel, Throwable e, boolean isRcv) {
        PipeListener.super.onError(channel, e, isRcv);
    }
}
