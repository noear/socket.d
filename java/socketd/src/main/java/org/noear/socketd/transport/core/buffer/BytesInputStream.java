package org.noear.socketd.transport.core.buffer;

import java.io.ByteArrayInputStream;

/**
 * @author noear
 * @since 2.0
 */
public class BytesInputStream extends ByteArrayInputStream {
    public BytesInputStream(byte[] buf) {
        super(buf);
    }

    /**
     * 获取 bytes
     */
    public byte[] bytes() {
        return buf;
    }
}
