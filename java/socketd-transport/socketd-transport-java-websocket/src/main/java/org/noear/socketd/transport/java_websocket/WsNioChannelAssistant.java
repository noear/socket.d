package org.noear.socketd.transport.java_websocket;

import org.java_websocket.WebSocket;
import org.noear.socketd.transport.core.ChannelAssistant;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.codec.ByteBufferCodecReader;
import org.noear.socketd.transport.core.codec.ByteBufferCodecWriter;
import org.noear.socketd.utils.IoCompletionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Ws-Bio 通道助理实现
 *
 * @author noear
 * @since 2.0
 */
public class WsNioChannelAssistant implements ChannelAssistant<WebSocket> {
    private final Config config;

    public WsNioChannelAssistant(Config config) {
        this.config = config;
    }

    @Override
    public void write(WebSocket source, Frame frame, ChannelInternal channel, IoCompletionHandler completionHandler) {
        try {
            ByteBufferCodecWriter writer = config.getCodec().write(frame, len -> new ByteBufferCodecWriter(ByteBuffer.allocate(len)));
            source.send(writer.getBuffer());

            completionHandler.completed(true, null);
        } catch (Throwable e) {
            completionHandler.completed(false, e);
        }
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
        return config.getCodec().read(new ByteBufferCodecReader(buffer));
    }
}
