package org.noear.socketd.transport.netty.tcp.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.noear.socketd.core.Config;
import org.noear.socketd.core.Frame;

import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.0
 */
public class NettyMessageEncoder extends MessageToByteEncoder<Frame> {
    private final Config config;

    public NettyMessageEncoder(Config config) {
        this.config = config;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Frame message, ByteBuf byteBuf) throws Exception {
        if (message != null) {
            ByteBuffer buf = config.getCodec().encode(message);
            byteBuf.writeBytes(buf.array());
        }
    }
}
