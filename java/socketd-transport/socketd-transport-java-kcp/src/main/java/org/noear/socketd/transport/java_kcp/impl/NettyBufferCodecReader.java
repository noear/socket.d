package org.noear.socketd.transport.java_kcp.impl;

import io.netty.buffer.ByteBuf;
import org.noear.socketd.transport.core.CodecReader;

/**
 * @author noear
 * @since 2.0
 */
public class NettyBufferCodecReader implements CodecReader {
    private ByteBuf source;
    public NettyBufferCodecReader(ByteBuf source){
        this.source = source;
    }
    @Override
    public byte getByte() {
        return source.readByte();
    }

    @Override
    public void getBytes(byte[] dst, int offset, int length) {
        source.readBytes(dst, offset, length);
    }

    @Override
    public int getInt() {
        return source.readInt();
    }

    @Override
    public void skipBytes(int length) {
        source.skipBytes(length);
    }

    @Override
    public int remaining() {
        return source.readableBytes();
    }

    @Override
    public int position() {
        return source.readerIndex();
    }
}
