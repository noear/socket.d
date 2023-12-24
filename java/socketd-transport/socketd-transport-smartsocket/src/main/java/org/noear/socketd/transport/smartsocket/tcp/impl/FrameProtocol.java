package org.noear.socketd.transport.smartsocket.tcp.impl;

import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.buffer.ByteBufferReader;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.extension.decoder.FixedLengthFrameDecoder;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.1
 */
public class FrameProtocol implements Protocol<Frame> {
    private final ChannelSupporter<AioSession> channelSupporter;

    public FrameProtocol(ChannelSupporter<AioSession> channelSupporter) {
        this.channelSupporter = channelSupporter;
    }

    private ChannelDefaultEx getChannel(AioSession s) {
        return ChannelDefaultEx.get(s, channelSupporter);
    }

    @Override
    public Frame decode(ByteBuffer buffer, AioSession aioSession){
        FixedLengthFrameDecoder decoder = getChannel(aioSession).getDecoder();

        if (decoder == null) {
            if (buffer.remaining() < Integer.BYTES) {
                return null;
            } else {
                buffer.mark();
                decoder = new FixedLengthFrameDecoder(buffer.getInt());
                buffer.reset();
                getChannel(aioSession).setDecoder(decoder);
            }
        }

        if (decoder.decode(buffer) == false) {
            return null;
        } else {
            getChannel(aioSession).setDecoder(null);
            buffer = decoder.getBuffer();
        }

        return channelSupporter.config().getCodec().read(new ByteBufferReader(buffer));
    }
}
