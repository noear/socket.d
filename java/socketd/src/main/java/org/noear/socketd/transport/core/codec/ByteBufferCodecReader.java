package org.noear.socketd.transport.core.codec;

import org.noear.socketd.transport.core.CodecReader;

import java.nio.ByteBuffer;

/**
 * 缓冲读 ByteBuffer 适配
 *
 * @author noear
 * @since 2.0
 */
public class ByteBufferCodecReader implements CodecReader {
    private ByteBuffer buffer;

    public ByteBufferCodecReader(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     * 获取 byte
     */
    @Override
    public byte getByte() {
        return buffer.get();
    }

    /**
     * 获取一组 byte
     */
    @Override
    public void getBytes(byte[] dst, int offset, int length) {
        buffer.get(dst, offset, length);
    }

    /**
     * 获取 int
     */
    @Override
    public int getInt() {
        return buffer.getInt();
    }

    @Override
    public void skipBytes(int length) {
        buffer.position(buffer.position() + length);
    }

    /**
     * 剩余长度
     */
    @Override
    public int remaining() {
        return buffer.remaining();
    }

    /**
     * 当前位置
     */
    @Override
    public int position() {
        return buffer.position();
    }
}
