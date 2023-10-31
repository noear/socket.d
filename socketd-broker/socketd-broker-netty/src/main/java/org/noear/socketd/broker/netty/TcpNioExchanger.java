package org.noear.socketd.broker.netty;

import io.netty.channel.Channel;
import org.noear.socketd.protocol.Frame;
import org.noear.socketd.protocol.ChannelAssistant;

import java.io.IOException;
import java.io.NotActiveException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @author noear
 * @since 2.0
 */
public class TcpNioExchanger implements ChannelAssistant<Channel> {
    @Override
    public void write(Channel source, Frame frame) throws IOException {
        if (source.isActive()) {
            source.writeAndFlush(frame);
        } else {
            //触发自动重链
            throw new NotActiveException();
        }
    }

    @Override
    public boolean isValid(Channel target) {
        return target.isActive();
    }

    @Override
    public void close(Channel target) throws IOException {
        target.close();
    }

    @Override
    public InetAddress getRemoteAddress(Channel target) {
        if (target.remoteAddress() instanceof InetSocketAddress) {
            return ((InetSocketAddress) target.remoteAddress()).getAddress();
        } else {
            return null;
        }
    }

    @Override
    public InetAddress getLocalAddress(Channel target) {
        if (target.localAddress() instanceof InetSocketAddress) {
            return ((InetSocketAddress) target.localAddress()).getAddress();
        } else {
            return null;
        }
    }
}
