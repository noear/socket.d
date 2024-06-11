package org.noear.socketd.transport.netty.udp;

import io.netty.buffer.ByteBuf;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.codec.ByteBufferCodecReader;
import org.noear.socketd.transport.core.codec.ByteBufferCodecWriter;
import org.noear.socketd.transport.netty.udp.impl.DatagramTagert;
import org.noear.socketd.transport.core.ChannelAssistant;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.utils.IoCompletionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Udp-Nio 通道助理实现
 *
 * @author noear
 * @since 2.0
 */
public class UdpNioChannelAssistant implements ChannelAssistant<DatagramTagert> {
    private Config config;
    public UdpNioChannelAssistant(Config config){
        this.config = config;
    }
    @Override
    public void write(DatagramTagert target, Frame frame, ChannelInternal channel, IoCompletionHandler completionHandler) {
        try {
            ByteBufferCodecWriter writer = config.getCodec().write(frame, i -> new ByteBufferCodecWriter(ByteBuffer.allocate(i)));
            target.send(writer.getBuffer().array());

            completionHandler.completed(true, null);
        } catch (Throwable e) {
            completionHandler.completed(false, e);
        }
    }

    public Frame read(ByteBuf inBuf) throws Exception {
        if (inBuf.readableBytes() < Integer.BYTES) {
            return null;
        }

        inBuf.markReaderIndex();
        int len = inBuf.readInt();

        if (inBuf.readableBytes() < (len - Integer.BYTES)) {
            inBuf.resetReaderIndex();
            return null;
        }

        byte[] bytes = new byte[len - Integer.BYTES];
        inBuf.readBytes(bytes);

        ByteBuffer byteBuffer = ByteBuffer.allocate(len);
        byteBuffer.putInt(len);
        byteBuffer.put(bytes);
        byteBuffer.flip();

        return config.getCodec().read(new ByteBufferCodecReader(byteBuffer));
    }

    @Override
    public boolean isValid(DatagramTagert target) {
        return true;
    }

    @Override
    public void close(DatagramTagert target) throws IOException {
        target.close();
    }

    @Override
    public InetSocketAddress getRemoteAddress(DatagramTagert target) {
        return target.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress(DatagramTagert target) {
        return target.getLocalAddress();
    }
}
