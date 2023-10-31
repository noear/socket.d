package org.noear.socketd.broker.netty.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.noear.socketd.protocol.CodecByteBuffer;
import org.noear.socketd.protocol.Frame;

import java.nio.ByteBuffer;
import java.util.List;

public class NettyMessageDecoder extends ByteToMessageDecoder {
    private CodecByteBuffer codec = new CodecByteBuffer();

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
