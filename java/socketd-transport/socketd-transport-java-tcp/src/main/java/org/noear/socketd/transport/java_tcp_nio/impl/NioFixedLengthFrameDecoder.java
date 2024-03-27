package org.noear.socketd.transport.java_tcp_nio.impl;

import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.4
 */
public class NioFixedLengthFrameDecoder {
    private ByteBuffer buffer;
    private boolean finishRead;

    public NioFixedLengthFrameDecoder(int frameLength) {
        if (frameLength <= 0) {
            throw new IllegalArgumentException("frameLength must be a positive integer: " + frameLength);
        } else {
            this.buffer = ByteBuffer.allocate(frameLength);
        }
    }

    public boolean decode(ByteBuffer byteBuffer) {
        if (this.finishRead) {
            throw new RuntimeException("delimiter has finish read");
        } else {
            if (this.buffer.remaining() >= byteBuffer.remaining()) {
                this.buffer.put(byteBuffer);
            } else {
                int limit = byteBuffer.limit();
                byteBuffer.limit(byteBuffer.position() + this.buffer.remaining());
                this.buffer.put(byteBuffer);
                byteBuffer.limit(limit);
            }

            if (this.buffer.hasRemaining()) {
                return false;
            } else {
                this.buffer.flip();
                this.finishRead = true;
                return true;
            }
        }
    }

    public ByteBuffer getBuffer() {
        return this.buffer;
    }
}
