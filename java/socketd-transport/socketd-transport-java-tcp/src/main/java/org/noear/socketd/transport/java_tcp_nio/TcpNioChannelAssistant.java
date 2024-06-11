package org.noear.socketd.transport.java_tcp_nio;

import org.noear.socketd.transport.core.ChannelAssistant;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.codec.ByteBufferCodecReader;
import org.noear.socketd.transport.core.codec.ByteBufferCodecWriter;
import org.noear.socketd.transport.java_tcp_nio.impl.NioAttachment;
import org.noear.socketd.transport.java_tcp_nio.impl.NioFixedLengthFrameDecoder;
import org.noear.socketd.utils.IoCompletionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author noear
 * @since 2.4
 */
public class TcpNioChannelAssistant implements ChannelAssistant<SocketChannel> {
    private Config config;
    private static final ByteBuffer emptyBuffer = ByteBuffer.allocate(0);

    public Config getConfig() {
        return config;
    }

    public TcpNioChannelAssistant(Config config){
        this.config = config;
    }
    @Override
    public void write(SocketChannel target, Frame frame, ChannelInternal channel, IoCompletionHandler completionHandler) {
        try {
            ByteBuffer buffer = getConfig().getCodec().write(frame, i -> new ByteBufferCodecWriter(ByteBuffer.allocate(i))).getBuffer();
            target.write(buffer);

            completionHandler.completed(true, null);
        } catch (Throwable e) {
            completionHandler.completed(false, e);
        }
    }

    public Frame read(SocketChannel target, NioAttachment attachment, ByteBuffer buffer){
        NioFixedLengthFrameDecoder decoder = attachment.decoder;

        if (decoder == null) {
            if (buffer.remaining() < Integer.BYTES) {
                return null;
            } else {
                buffer.mark();
                decoder = new NioFixedLengthFrameDecoder(buffer.getInt());
                buffer.reset();
                attachment.decoder = decoder;
            }
        }

        if (decoder.decode(buffer) == false) {
            return null;
        } else {
            attachment.decoder = null;
            buffer = decoder.getBuffer();
        }

        return getConfig().getCodec().read(new ByteBufferCodecReader(buffer));
    }

    @Override
    public boolean isValid(SocketChannel target) {
        return target.isConnected();
    }

    @Override
    public void close(SocketChannel target) throws IOException {
        target.close();
    }

    @Override
    public InetSocketAddress getRemoteAddress(SocketChannel target) throws IOException {
        return (InetSocketAddress) target.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress(SocketChannel target) throws IOException {
        return (InetSocketAddress) target.getLocalAddress();
    }
}
