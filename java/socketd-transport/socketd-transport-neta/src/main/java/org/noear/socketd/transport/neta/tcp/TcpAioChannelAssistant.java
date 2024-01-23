package org.noear.socketd.transport.neta.tcp;

import net.hasor.neta.channel.NetChannel;
import org.noear.socketd.transport.core.ChannelAssistant;
import org.noear.socketd.transport.core.Frame;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author noear
 * @since 2.3
 */
public class TcpAioChannelAssistant implements ChannelAssistant<NetChannel> {
    @Override
    public void write(NetChannel target, Frame frame) throws IOException {
        target.sendData(frame);
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
