package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.bytebuf.ByteBuf;
import org.noear.socketd.transport.core.CodecReader;

/**
 * @author noear
 * @since 2.6
 */
public class ByteBufCodecReader implements CodecReader {
    private final ByteBuf byteBuf;

    public ByteBufCodecReader(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Override
    public byte getByte() {
        return byteBuf.readByte();
    }

    @Override
    public void getBytes(byte[] dst, int offset, int length) {
        byteBuf.readBytes(dst, offset, length);
    }

    @Override
    public int getInt() {
        return byteBuf.readInt32();
    }

    @Override
    public byte peekByte() {
        return byteBuf.getByte(0);
    }

    @Override
    public void skipBytes(int length) {
        byteBuf.skipReadableBytes(length);
    }

    @Override
    public int remaining() {
        return byteBuf.readableBytes();
    }

    @Override
    public int position() {
        return byteBuf.readerIndex();
    }
}
