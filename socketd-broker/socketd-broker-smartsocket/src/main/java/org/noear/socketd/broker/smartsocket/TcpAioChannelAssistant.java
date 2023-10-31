package org.noear.socketd.broker.smartsocket;

import org.noear.socketd.broker.smartsocket.impl.Attachment;
import org.noear.socketd.broker.smartsocket.impl.FixedLengthFrameDecoder;
import org.noear.socketd.protocol.CodecByteBuffer;
import org.noear.socketd.protocol.ChannelAssistant;
import org.noear.socketd.protocol.Frame;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Tcp-Aio 通道助理实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpAioChannelAssistant implements ChannelAssistant<AioSession>, Protocol<Frame> {
    private CodecByteBuffer codec = new CodecByteBuffer();

    @Override
    public void write(AioSession source, Frame frame) throws IOException {
        ByteBuffer buf = codec.encode(frame);
        source.writeBuffer().writeAndFlush(buf.array());
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
    public Frame decode(ByteBuffer buffer, AioSession aioSession) {
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

        return codec.decode(buffer);
    }
}
