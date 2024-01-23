package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.bytebuf.ByteBuf;
import org.noear.socketd.transport.core.CodecWriter;

import java.io.IOException;

/**
 * @author noear
 * @since 2.6
 */
public class ByteBufCodecWriter implements CodecWriter {
    private final ByteBuf byteBuf;

    public ByteBufCodecWriter(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Override
    public void putBytes(byte[] bytes) throws IOException {
        byteBuf.writeBytes(bytes);
    }

    @Override
    public void putInt(int val) throws IOException {
        byteBuf.writeInt32(val);
    }

    @Override
    public void putChar(int val) throws IOException {
        byteBuf.writeInt16((short) val);
    }

    @Override
    public void flush() throws IOException {
        byteBuf.flush();
    }

    public ByteBuf buffer() {
        return byteBuf;
    }
}
