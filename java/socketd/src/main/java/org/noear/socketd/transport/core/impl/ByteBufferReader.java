package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.BufferReader;

import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.0
 */
public class ByteBufferReader implements BufferReader {
    private ByteBuffer buffer;

    public ByteBufferReader(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public byte get() {
        return buffer.get();
    }

    @Override
    public void get(byte[] dst, int offset, int length) {
        buffer.get(dst, offset, length);
    }

    @Override
    public int getInt() {
        return buffer.getInt();
    }

    @Override
    public int remaining() {
        return buffer.remaining();
    }

    @Override
    public int position() {
        return buffer.position();
    }
}
