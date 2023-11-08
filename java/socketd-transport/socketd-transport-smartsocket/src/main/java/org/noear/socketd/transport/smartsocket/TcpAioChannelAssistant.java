package org.noear.socketd.transport.smartsocket;

import org.noear.socketd.transport.core.impl.ByteBufferReader;
import org.noear.socketd.transport.core.ChannelAssistant;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.smartsocket.impl.ChannelDefaultEx;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.extension.decoder.FixedLengthFrameDecoder;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Tcp-Aio 通道助理实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpAioChannelAssistant implements ChannelAssistant<AioSession>, Protocol<Frame> {
    private final Config config;

    public TcpAioChannelAssistant(Config config) {
        this.config = config;
    }

    private ChannelDefaultEx getChannel(AioSession s) {
        return ChannelDefaultEx.get(s, config, this);
    }

    @Override
    public void write(AioSession source, Frame frame) throws IOException {
        config.getCodec().write(frame, i -> new TcpAioBufferWriter(source.writeBuffer()));
    }

    @Override
    public boolean isValid(AioSession target) {
        return target.isInvalid() == false;
    }

    @Override
    public void close(AioSession target) throws IOException {
        target.close();
    }

    @Override
    public InetSocketAddress getRemoteAddress(AioSession target) throws IOException{
        return target.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress(AioSession target) throws IOException{
        return target.getLocalAddress();
    }

    @Override
    public Frame decode(ByteBuffer buffer, AioSession aioSession){
//        if (buffer.remaining() < Integer.BYTES) {
//            return null;
//        }
//        buffer.mark();
//        int length = buffer.getInt() - Integer.BYTES;
//        if (length > buffer.remaining()) {
//            buffer.reset();
//            return null;
//        }
//        buffer.reset();
//
//        return config.getCodec().read(new ByteBufferReader(buffer));

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

        return config.getCodec().read(new ByteBufferReader(buffer));
    }
}
