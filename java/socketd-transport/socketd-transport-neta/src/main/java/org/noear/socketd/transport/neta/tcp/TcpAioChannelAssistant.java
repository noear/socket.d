package org.noear.socketd.transport.neta.tcp;

import net.hasor.neta.channel.NetChannel;
import org.noear.socketd.transport.core.ChannelAssistant;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Frame;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author noear
 * @since 2.3
 */
public class TcpAioChannelAssistant implements ChannelAssistant<NetChannel> {
    @Override
    public void write(NetChannel target, Frame frame, ChannelInternal channel) throws IOException {
        try {
            channel.writeAcquire(frame);

            target.sendData(frame).onFinal(future -> {
                channel.writeRelease(frame);
            });
        } catch (Throwable e) {
            channel.writeRelease(frame);

            if (e instanceof IOException) {
                throw e;
            } else {
                throw new IOException(e);
            }
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
