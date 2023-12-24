package org.noear.socketd.transport.java_kcp.impl;

import io.netty.buffer.ByteBuf;
import org.noear.socketd.transport.core.buffer.BufferReader;

/**
 * @author noear
 * @since 2.0
 */
public class NettyBufferReader implements BufferReader {
    private ByteBuf source;
    public NettyBufferReader(ByteBuf source){
        this.source = source;
    }
    @Override
    public byte get() {
        return source.readByte();
    }

    @Override
    public void get(byte[] dst, int offset, int length) {
        source.readBytes(dst, offset, length);
    }

    @Override
    public int getInt() {
        return source.readInt();
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
