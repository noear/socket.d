package org.noear.socketd.transport.java_tcp;

import org.noear.socketd.transport.core.buffer.BufferWriter;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author noear
 * @since 2.0
 */
public class OutputStreamBufferWriter implements BufferWriter {
    private OutputStream target;

    public OutputStreamBufferWriter(OutputStream target) {
        this.target = target;
    }

    @Override
    public void putBytes(byte[] bytes) throws IOException {
        target.write(bytes);
    }

    @Override
    public void putBytes(byte[] src, int offset, int length) throws IOException {
        target.write(src, offset, length);
    }

    @Override
    public void putInt(int val) throws IOException {
        target.write((val >>> 24) & 0xFF);
        target.write((val >>> 16) & 0xFF);
        target.write((val >>> 8) & 0xFF);
        target.write((val >>> 0) & 0xFF);
    }

    @Override
    public void putChar(int val) throws IOException {
        target.write((val >>> 8) & 0xFF);
        target.write((val >>> 0) & 0xFF);
    }

    @Override
    public void flush() throws IOException {
        target.flush();
    }
}
