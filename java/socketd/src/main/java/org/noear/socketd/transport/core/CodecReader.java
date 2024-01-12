package org.noear.socketd.transport.core;

/**
 * 编解码缓冲读
 *
 * @author noear
 * @since 2.0
 */
public interface CodecReader {

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

    /*
     * 预看 byte
     * */
    byte peekByte();

    /**
     * 跳过
     */
    void skipBytes(int length);

    /**
     * 剩余长度
     */
    int remaining();

    /**
     * 当前位置
     */
    int position();
}
