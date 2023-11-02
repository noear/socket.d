package org.noear.socketd.broker.netty.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.noear.socketd.core.Codec;
import org.noear.socketd.core.Frame;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author noear
 * @since 2.0
 */
public class NettyMessageDecoder extends ByteToMessageDecoder {
    private final Codec<ByteBuffer> codec;

    public NettyMessageDecoder(Codec<ByteBuffer> codec) {
        this.codec = codec;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        int len = byteBuf.readInt();
        if(len > 0) {
            byte[] bytes = new byte[len - Integer.BYTES];
            byteBuf.readBytes(bytes);

            ByteBuffer byteBuffer = ByteBuffer.allocate(len);
            byteBuffer.putInt(len);
            byteBuffer.put(bytes);
            byteBuffer.flip();

            Frame message = codec.decode(byteBuffer);
            if (message != null) {
                out.add(message);
            }

            byteBuffer.compact();
        }
        byteBuf.discardReadBytes();
    }
}
