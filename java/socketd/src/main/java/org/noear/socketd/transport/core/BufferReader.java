package org.noear.socketd.transport.core;

/**
 * @author noear
 * @since 2.0
 */
public interface BufferReader {

    byte get();

    void get(byte[] dst, int offset, int length);

    int getInt();

    int remaining();

    int position();
}
