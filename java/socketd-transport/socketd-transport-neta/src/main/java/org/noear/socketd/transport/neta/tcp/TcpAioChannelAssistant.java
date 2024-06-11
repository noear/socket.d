package org.noear.socketd.transport.neta.tcp;

import net.hasor.neta.channel.NetChannel;
import org.noear.socketd.transport.core.ChannelAssistant;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.utils.IoCompletionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author noear
 * @since 2.3
 */
public class TcpAioChannelAssistant implements ChannelAssistant<NetChannel> {
    @Override
    public void write(NetChannel target, Frame frame, ChannelInternal channel, IoCompletionHandler completionHandler) {
        try {
            target.sendData(frame).onFinal(future -> {
                if (future.getCause() == null) {
                    completionHandler.completed(true, null);
                } else {
                    completionHandler.completed(false, future.getCause());
                }
            });
        } catch (Throwable e) {
            completionHandler.completed(false, e);
        }
    }

    @Override
    public boolean isValid(NetChannel target) {
        return !target.isClose();
    }

    @Override
    public void close(NetChannel target) throws IOException {
        target.close();
    }

    @Override
    public InetSocketAddress getRemoteAddress(NetChannel target) throws IOException {
        return (InetSocketAddress) target.getRemoteAddr();
    }

    @Override
    public InetSocketAddress getLocalAddress(NetChannel target) throws IOException {
        return (InetSocketAddress) target.getLocalAddr();
    }
}
