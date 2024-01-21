package org.noear.socketd.transport.neta.tcp.impl;

import net.hasor.neta.bytebuf.ByteBuf;
import org.noear.socketd.transport.core.CodecReader;

/**
 * @author noear 2024/1/21 created
 */
public class ByteBufCodecReader implements CodecReader {
    public ByteBufCodecReader(ByteBuf byteBuf){

    }
    @Override
    public byte getByte() {
        return 0;
    }

    @Override
    public void getBytes(byte[] dst, int offset, int length) {

    }

    @Override
    public int getInt() {
        return 0;
    }

    @Override
    public byte peekByte() {
        return 0;
    }

    @Override
    public void skipBytes(int length) {

    }

    @Override
    public int remaining() {
        return 0;
    }

    @Override
    public int position() {
        return 0;
    }
}
