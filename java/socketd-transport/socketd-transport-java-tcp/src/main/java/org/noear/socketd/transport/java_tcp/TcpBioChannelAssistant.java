package org.noear.socketd.transport.java_tcp;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.codec.ByteBufferCodecReader;
import org.noear.socketd.transport.core.codec.ByteBufferCodecWriter;
import org.noear.socketd.utils.IoCompletionHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Tcp-Bio 交换器实现（它没法固定接口，但可以固定输出目录）
 *
 * @author noear
 * @since 2.0
 */
public class TcpBioChannelAssistant implements ChannelAssistant<Socket> {
    private final Config config;

    public TcpBioChannelAssistant(Config config) {
        this.config = config;
    }



    @Override
    public boolean isValid(Socket target) {
        return target.isConnected();
    }

    @Override
    public void close(Socket target) throws IOException {
        target.close();
    }

    @Override
    public InetSocketAddress getRemoteAddress(Socket target) {
        return (InetSocketAddress) target.getRemoteSocketAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress(Socket target) {
        return (InetSocketAddress) target.getLocalSocketAddress();
    }

    @Override
    public void write(Socket source, Frame frame, ChannelInternal channel, IoCompletionHandler completionHandler) {
        try {
            OutputStream output = source.getOutputStream();
            ByteBuffer buffer = config.getCodec().write(frame, (i) -> new ByteBufferCodecWriter(ByteBuffer.allocate(i))).getBuffer();
            output.write(buffer.array());
            output.flush();

            completionHandler.completed(true, null);
        } catch (Throwable e) {
            completionHandler.completed(false, e);
        }
    }

    public Frame read(Socket source) throws IOException {
        InputStream input = source.getInputStream();
        if (input == null) {
            return null;
        }

        byte[] lenBts = new byte[4];
        if (input.read(lenBts) == -1) {
            return null;
        }

        int len = bytesToInt32(lenBts);

        if (len == 0) {
            return null;
        }

        if(len > Constants.MAX_SIZE_FRAME) {
            //如果超时，跳
            source.getInputStream().skip(len - Integer.BYTES);
            return null;
        }

        ByteBuffer buffer = ByteBuffer.allocate(len);
        buffer.putInt(len);

        int bufSize = config.getReadBufferSize();
        byte[] buf = new byte[bufSize];

        int readSize = 0;

        while (true) {
            if (buffer.remaining() > bufSize) {
                readSize = bufSize;
            } else {
                readSize = buffer.remaining();
            }

            if ((readSize = input.read(buf, 0, readSize)) > 0) {
                buffer.put(buf, 0, readSize);
            } else {
                break;
            }
        }

        buffer.flip();

        return config.getCodec().read(new ByteBufferCodecReader(buffer));
    }

    private static int bytesToInt32(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (3 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }
}
