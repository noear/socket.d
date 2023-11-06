package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.BufferWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 缓冲写 ByteBuffer 适配
 *
 * @author noear
 * @since 2.0
 */
public class ByteBufferWriter implements BufferWriter {
    private ByteBuffer target;

    public ByteBufferWriter(ByteBuffer target) {
        this.target = target;
    }

    /**
     * 推入一组 byte
     */
    @Override
    public void putBytes(byte[] src) {
        target.put(src);
    }

    /**
     * 推入一组 byte
     */
    @Override
    public void putBytes(byte[] src, int offset, int length) {
        target.put(src, offset, length);
    }

    /**
     * 推入 int
     */
    @Override
    public void putInt(int val) {
        target.putInt(val);
    }

    /**
     * 推入 char
     */
    @Override
    public void putChar(int val) {
        target.putChar((char) val);
    }

    @Override
    public void flush() throws IOException {
        target.flip();
    }

    /**
     * 冲刷
     */
    public ByteBuffer getBuffer() {
        return target;
    }
}
