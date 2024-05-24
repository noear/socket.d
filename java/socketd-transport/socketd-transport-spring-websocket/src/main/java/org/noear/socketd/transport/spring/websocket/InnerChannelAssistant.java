package org.noear.socketd.transport.spring.websocket;

import org.noear.socketd.transport.core.ChannelAssistant;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.codec.ByteBufferCodecReader;
import org.noear.socketd.transport.core.codec.ByteBufferCodecWriter;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.4
 */
class InnerChannelAssistant implements ChannelAssistant<WebSocketSession> {
    private final Config config;

    public InnerChannelAssistant(Config config) {
        this.config = config;
    }

    @Override
    public void write(WebSocketSession target, Frame frame, ChannelInternal channel) throws IOException {
        try {
            channel.writeAcquire(frame);

            ByteBufferCodecWriter writer = config.getCodec().write(frame, len -> new ByteBufferCodecWriter(ByteBuffer.allocate(len)));
            target.sendMessage(new BinaryMessage(writer.getBuffer()));
        } finally {
            channel.writeRelease(frame);
        }
    }

    public Frame read(ByteBuffer buffer) throws IOException {
        return config.getCodec().read(new ByteBufferCodecReader(buffer));
    }

    @Override
    public boolean isValid(WebSocketSession target) {
        return target.isOpen();
    }

    @Override
    public void close(WebSocketSession target) throws IOException {
        target.close();
    }

    @Override
    public InetSocketAddress getRemoteAddress(WebSocketSession target) throws IOException {
        return target.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress(WebSocketSession target) throws IOException {
        return target.getLocalAddress();
    }
}
