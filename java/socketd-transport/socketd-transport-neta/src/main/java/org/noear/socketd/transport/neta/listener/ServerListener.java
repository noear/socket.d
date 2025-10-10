package org.noear.socketd.transport.neta.listener;

import net.hasor.neta.channel.*;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.Processor;

/**
 * @author noear
 * @since 2.3
 */
public class ServerListener implements PlayLoadListener {
    private final Processor processor;

    public ServerListener(ChannelSupporter<NetChannel> supporter) {
        this.processor = supporter.getProcessor();
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

    private void onData(ChannelInternal channel, Frame src) {
        processor.reveFrame(channel, src);
    }

    private void onError(ChannelInternal channel, Throwable e) {
        if (e instanceof SoCloseException) {
            processor.onClose(channel);
        } else if (e instanceof SoTimeoutException) {
            processor.onError(channel, e);
        } else {
            processor.onError(channel, e);
        }
    }
}
