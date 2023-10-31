package org.noear.socketd.broker.netty.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.noear.socketd.protocol.CodecByteBuffer;
import org.noear.socketd.protocol.Frame;

import java.nio.ByteBuffer;

public class NettyMessageEncoder extends MessageToByteEncoder<Frame> {
    private CodecByteBuffer codec = new CodecByteBuffer();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Frame message, ByteBuf byteBuf) throws Exception {
        if (message != null) {
            ByteBuffer buf = codec.encode(message);
            byteBuf.writeBytes(buf.array());
        }
    }
}
