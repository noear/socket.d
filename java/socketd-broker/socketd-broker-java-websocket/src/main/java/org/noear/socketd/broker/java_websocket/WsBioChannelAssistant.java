package org.noear.socketd.broker.java_websocket;

import org.java_websocket.WebSocket;
import org.noear.socketd.core.Codec;
import org.noear.socketd.core.CodecByteBuffer;
import org.noear.socketd.core.Frame;
import org.noear.socketd.core.ChannelAssistant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Ws-Bio 通道助理实现
 *
 * @author noear
 * @since 2.0
 */
public class WsBioChannelAssistant implements ChannelAssistant<WebSocket> {
    private final Codec<ByteBuffer> codec;

    public WsBioChannelAssistant(Codec<ByteBuffer> codec) {
        this.codec = codec;
    }

    @Override
    public void write(WebSocket source, Frame frame) throws IOException {
        source.send(codec.encode(frame));
    }

    @Override
    public boolean isValid(WebSocket target) {
        return target.isOpen();
    }

    @Override
    public void close(WebSocket target) throws IOException {
        target.close();
    }

    @Override
    public InetSocketAddress getRemoteAddress(WebSocket target) {
        return target.getRemoteSocketAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress(WebSocket target) {
        return target.getLocalSocketAddress();
    }

    public Frame read(ByteBuffer buffer) throws IOException{
        return codec.decode(buffer);
    }
}
