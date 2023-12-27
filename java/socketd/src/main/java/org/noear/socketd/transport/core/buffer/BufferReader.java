package org.noear.socketd.transport.core.buffer;

/**
 * 缓冲读
 *
 * @author noear
 * @since 2.0
 */
public interface BufferReader {

    /**
     * 获取 byte
     */
    byte getByte();

    /**
     * 获取一组 byte
     */
    void getBytes(byte[] dst, int offset, int length);

    /**
     * 获取 int
     */
    int getInt();

    /**
     * 剩余长度
     */
    int remaining();

    /**
     * 当前位置
     */
    int position();
}
