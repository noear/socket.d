package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.BufferWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.0
 */
public class ByteBufferWriter implements BufferWriter {
    private ByteBuffer target;

    public ByteBufferWriter(ByteBuffer target) {
        this.target = target;
    }

    @Override
    public void putBytes(byte[] src) {
        target.put(src);
    }

    @Override
    public void putBytes(byte[] src, int offset, int length) {
        target.put(src, offset, length);
    }

    @Override
    public void putInt(int val) {
        target.putInt(val);
    }

    @Override
    public void putChar(int val) {
        target.putChar((char) val);
    }

    @Override
    public void flush() throws IOException {
        target.flip();
    }

    public ByteBuffer getBuffer() {
        return target;
    }
}
