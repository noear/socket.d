package org.noear.socketd.transport.smartsocket;

import org.noear.socketd.transport.core.impl.ByteBufferReader;
import org.noear.socketd.transport.smartsocket.impl.Attachment;
import org.noear.socketd.transport.smartsocket.impl.FixedLengthFrameDecoder;
import org.noear.socketd.transport.core.ChannelAssistant;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;
import org.smartboot.socket.Protocol;
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
        FixedLengthFrameDecoder decoder = Attachment.getDecoder(aioSession);

        if (decoder == null) {
            if (buffer.remaining() < Integer.BYTES) {
                return null;
            } else {
                buffer.mark();
                decoder = new FixedLengthFrameDecoder(buffer.getInt());
                buffer.reset();
                Attachment.setDecoder(aioSession, decoder);
            }
        }

        if (decoder.read(buffer) == false) {
            return null;
        } else {
            Attachment.setDecoder(aioSession, null);
            buffer = decoder.getBuffer();
            buffer.flip();
        }

        return config.getCodec().read(new ByteBufferReader(buffer));
    }
}
