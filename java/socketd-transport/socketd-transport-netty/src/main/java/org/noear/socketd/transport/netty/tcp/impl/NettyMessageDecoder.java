package org.noear.socketd.transport.netty.tcp.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.noear.socketd.transport.core.buffer.BufferReader;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;

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
        BufferReader reader = new NettyBufferReader(inBuf);
        Frame message = config.getCodec().read(reader);
        if (message == null) {
            return;
        }

        out.add(message);
    }
}
