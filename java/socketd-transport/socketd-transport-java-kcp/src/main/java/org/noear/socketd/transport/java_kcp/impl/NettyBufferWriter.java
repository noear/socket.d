package org.noear.socketd.transport.java_kcp.impl;

import io.netty.buffer.ByteBuf;
import org.noear.socketd.transport.core.buffer.BufferWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.0
 */
public class NettyBufferWriter implements BufferWriter {
    private ByteBuf target;

    public NettyBufferWriter(ByteBuf target) {
        this.target = target;
    }

    @Override
    public void putBytes(byte[] bytes) throws IOException {
        target.writeBytes(bytes);
    }

    @Override
    public void putBytes(byte[] src, int offset, int length) throws IOException {
        target.writeBytes(src, offset, length);
    }

    @Override
    public void putInt(int val) throws IOException {
        target.writeInt(val);
    }

    @Override
    public void putChar(int val) throws IOException {
        target.writeChar(val);
    }

    @Override
    public void flush() throws IOException {

    }

    public ByteBuf getBuffer() {
        return target;
    }
}
