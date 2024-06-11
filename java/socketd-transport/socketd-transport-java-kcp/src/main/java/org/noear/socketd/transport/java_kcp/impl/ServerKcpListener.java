package org.noear.socketd.transport.java_kcp.impl;

import io.netty.buffer.ByteBuf;
import kcp.KcpListener;
import kcp.Ukcp;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.CodecReader;
import org.noear.socketd.transport.core.impl.ChannelDefault;
import org.noear.socketd.transport.java_kcp.KcpNioServer;

/**
 * @author noear
 * @since 2.1
 */
public class ServerKcpListener implements KcpListener {
    private final KcpNioServer server;

    public ServerKcpListener(KcpNioServer server) {
        this.server = server;
    }

    @Override
    public void onConnected(Ukcp ukcp) {
        ChannelInternal channel = new ChannelDefault<>(ukcp, server);
        ukcp.user().setCache(channel);
    }

    @Override
    public void handleReceive(ByteBuf byteBuf, Ukcp ukcp) {
        CodecReader reader = new NettyBufferCodecReader(byteBuf);
        Frame frame = server.getConfig().getCodec().read(reader);
        if (frame == null) {
            return;
        }

        ChannelInternal channel = ukcp.user().getCache();

        try {
            server.getProcessor().reveFrame(channel, frame);
        } catch (Throwable e) {
            server.getProcessor().onError(channel, e);
        }
    }

    @Override
    public void handleException(Throwable throwable, Ukcp ukcp) {
        ChannelInternal channel = ukcp.user().getCache();
        server.getProcessor().onError(channel, throwable);
    }

    @Override
    public void handleClose(Ukcp ukcp) {
        ChannelInternal channel = ukcp.user().getCache();
        server.getProcessor().onClose(channel);
    }
}
