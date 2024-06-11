package org.noear.socketd.transport.java_kcp;

import io.netty.buffer.Unpooled;
import kcp.Ukcp;
import org.noear.socketd.transport.core.ChannelAssistant;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.java_kcp.impl.NettyBufferCodecWriter;
import org.noear.socketd.utils.IoCompletionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author noear
 * @since 2.1
 */
public class KcpNioChannelAssistant implements ChannelAssistant<Ukcp> {
    private final Config config;

    public KcpNioChannelAssistant(Config config) {
        this.config = config;
    }

    @Override
    public void write(Ukcp target, Frame frame, ChannelInternal channel, IoCompletionHandler completionHandler) {
        try {
            NettyBufferCodecWriter writer = config.getCodec().write(frame, i -> new NettyBufferCodecWriter(Unpooled.buffer(i)));
            target.write(writer.getBuffer());
            writer.getBuffer().release();

            completionHandler.completed(true, null);
        } catch (Throwable e) {
            completionHandler.completed(false, e);
        }
    }

    @Override
    public boolean isValid(Ukcp target) {
        return target.isActive();
    }

    @Override
    public void close(Ukcp target) throws IOException {
        target.close();
    }

    @Override
    public InetSocketAddress getRemoteAddress(Ukcp target) throws IOException {
        return target.user().getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress(Ukcp target) throws IOException {
        return null; //target.user().getLocalAddress();
    }
}
