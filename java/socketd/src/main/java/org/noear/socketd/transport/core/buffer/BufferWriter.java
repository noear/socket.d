package org.noear.socketd.transport.core.buffer;

import java.io.IOException;

/**
 * 缓冲写
 *
 * @author noear
 * @since 2.0
 */
public interface BufferWriter {
    /**
     * 推入一组 byte
     */
    void putBytes(byte[] bytes) throws IOException;

    /**
     * 推入一组 byte
     */
    void putBytes(byte[] src, int offset, int length) throws IOException;

    /**
     * 推入 int
     */
    void putInt(int val) throws IOException;

    /**
     * 推入 char
     */
    void putChar(int val) throws IOException;

    /**
     * 冲刷
     */
    void flush() throws IOException;
}
