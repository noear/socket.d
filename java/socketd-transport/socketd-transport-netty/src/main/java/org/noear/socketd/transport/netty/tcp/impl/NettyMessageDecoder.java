package org.noear.socketd.transport.netty.tcp.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author noear
 * @since 2.0
 */
public class NettyMessageDecoder extends ByteToMessageDecoder {
    private final Config config;

    public NettyMessageDecoder(Config config) {
        this.config = config;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf inBuf, List<Object> out) throws Exception {
        if (inBuf.readableBytes() < Integer.BYTES) {
            return;
        }

        inBuf.markReaderIndex();
        int len = inBuf.readInt();

        if (inBuf.readableBytes() < (len - Integer.BYTES)) {
            inBuf.resetReaderIndex();
            return;
        }

        if (len > 0) {
            byte[] bytes = new byte[len - Integer.BYTES];
            inBuf.readBytes(bytes);

            ByteBuffer byteBuffer = ByteBuffer.allocate(len);
            byteBuffer.putInt(len);
            byteBuffer.put(bytes);
            byteBuffer.flip();

            Frame message = config.getCodec().decode(byteBuffer);
            if (message != null) {
                out.add(message);
            }
        }

        //弃用已读的空间，从而复用
        inBuf.discardReadBytes();
    }
}
