package org.noear.socketd.transport.smartsocket.tcp;

import io.github.smartboot.socket.transport.WriteBuffer;
import org.noear.socketd.transport.core.CodecWriter;

import java.io.IOException;

/**
 * @author noear
 * @since 2.0
 */
public class TcpAioBufferWriter implements CodecWriter {
    private WriteBuffer target;

    public TcpAioBufferWriter(WriteBuffer target) {
        this.target = target;
    }

    @Override
    public void putBytes(byte[] bytes) throws IOException {
        target.write(bytes);
    }

    @Override
    public void putInt(int val) throws IOException {
        target.writeInt(val);
    }

    @Override
    public void putChar(int val) throws IOException {
        target.writeShort((short) val);
    }

    @Override
    public void flush() throws IOException {
        target.flush();
    }
}
