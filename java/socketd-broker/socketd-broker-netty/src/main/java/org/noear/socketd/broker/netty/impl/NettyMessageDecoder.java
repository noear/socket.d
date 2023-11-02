package org.noear.socketd.broker.netty.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.noear.socketd.core.Config;
import org.noear.socketd.core.Frame;

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
        int len = inBuf.readInt();

        if(len > 0) {
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

            byteBuffer.compact(); //时间久了，都不知道这是干嘛的
        }

        inBuf.discardReadBytes(); //时间久了，都不知道这是干嘛的
    }
}
