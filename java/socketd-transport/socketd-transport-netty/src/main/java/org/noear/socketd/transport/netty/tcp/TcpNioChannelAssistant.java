package org.noear.socketd.transport.netty.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.ChannelAssistant;

import java.io.IOException;
import java.io.NotActiveException;
import java.net.InetSocketAddress;

/**
 * Tcp-Nio 通道助理实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpNioChannelAssistant implements ChannelAssistant<Channel> {
    @Override
    public void write(Channel target, Frame frame, ChannelInternal channel) throws IOException {
        if (target.isActive()) {
            try {
                ChannelPromise channelPromise = target.newPromise();
                channelPromise.addListener(future -> {
                    channel.writeRelease(frame);
                });

                channel.writeAcquire(frame);
                target.writeAndFlush(frame, channelPromise);
            } catch (Throwable e) {
                channel.writeRelease(frame);

                if (e instanceof IOException) {
                    throw e;
                } else {
                    throw new IOException(e);
                }
            }
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
    public InetSocketAddress getRemoteAddress(Channel target) {
        return (InetSocketAddress) target.remoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress(Channel target) {
        return (InetSocketAddress) target.localAddress();
    }
}
