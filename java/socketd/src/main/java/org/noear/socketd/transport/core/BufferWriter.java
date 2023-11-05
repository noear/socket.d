package org.noear.socketd.transport.core;

import java.io.IOException;

/**
 * @author noear
 * @since 2.0
 */
public interface BufferWriter {
    void putBytes(byte[] bytes) throws IOException;
    void putBytes(byte[] src, int offset, int length) throws IOException;

    void putInt(int val) throws IOException;
    void putChar(int val) throws IOException;
    void flush() throws IOException;
}
