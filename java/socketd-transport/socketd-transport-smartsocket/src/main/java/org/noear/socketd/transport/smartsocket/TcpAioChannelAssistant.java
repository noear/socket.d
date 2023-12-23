package org.noear.socketd.transport.smartsocket;

import org.noear.socketd.transport.core.*;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;

/**
 * Tcp-Aio 通道助理实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpAioChannelAssistant implements ChannelAssistant<AioSession> {
    private final Config config;

    public TcpAioChannelAssistant(Config config) {
        this.config = config;
    }

    @Override
    public void write(AioSession source, Frame frame) throws IOException {
        config.getCodec().write(frame, i -> new TcpAioBufferWriter(source.writeBuffer()));
    }

    @Override
    public boolean isValid(AioSession target) {
        return target.isInvalid() == false;
    }

    @Override
    public void close(AioSession target) throws IOException {
        if (target.isInvalid() == false) {
            target.close();
        }
    }

    @Override
    public InetSocketAddress getRemoteAddress(AioSession target) throws IOException {
        return target.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress(AioSession target) throws IOException {
        return target.getLocalAddress();
    }
}
