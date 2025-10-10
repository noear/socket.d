package org.noear.socketd.transport.neta.socket;

import net.hasor.neta.channel.*;
import net.hasor.neta.handler.codec.LengthFieldBasedFrameHandler;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.neta.codec.FrameDecoder;
import org.noear.socketd.transport.neta.codec.FrameEncoder;
import org.noear.socketd.transport.neta.listener.ServerListener;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;

/**
 * @author noear
 * @since 2.3
 */
public abstract class AioServer extends ServerBase<AioChannelAssistant> implements ChannelSupporter<NetChannel> {
    private static final Logger     log = LoggerFactory.getLogger(AioServer.class);
    private              NetManager server;

    public AioServer(ServerConfig config) {
        super(config, new AioChannelAssistant());
    }

    protected abstract NetListen bindTo(NetManager neta, InetSocketAddress bindAddr, ProtoInitializer initializer) throws IOException;

    @Override
    public Server start() throws IOException {
        if (isStarted) {
            throw new IllegalStateException("Socket.D server started");
        } else {
            isStarted = true;
        }

        InetSocketAddress bindAddr = StrUtils.isEmpty(getConfig().getHost()) ? new InetSocketAddress(getConfig().getPort()) : new InetSocketAddress(getConfig().getHost(), getConfig().getPort());
        NetConfig netConfig = new NetConfig();
        netConfig.setPrintLog(true);

        server = new NetManager(netConfig);
        server.subscribe(PlayLoad::isInbound, new ServerListener(this));
        this.bindTo(this.server, bindAddr, ctx -> {
            // ssl
            if (AioSslHelper.isUsingSSL(getConfig())) {
                ctx.addLast("SSL", AioSslHelper.createSSL(getConfig()));
            }
            // frame
            ctx.addLastDecoder(new LengthFieldBasedFrameHandler(0, ByteOrder.BIG_ENDIAN, 4, 0, -4, Constants.MAX_SIZE_FRAME));
            // codec
            FrameDecoder decoder = new FrameDecoder(this.getConfig(), this);
            FrameEncoder encoder = new FrameEncoder(this.getConfig(), this);
            ctx.addLast(decoder, encoder);
        });

        log.info("Socket.D server started: {server=" + getConfig().getLocalUrl() + "}");

        return this;
    }

    @Override
    public void stop() {
        if (isStarted) {
            isStarted = false;
        } else {
            return;
        }

        super.stop();

        try {
            if (server != null) {
                server.shutdown();
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Server stop error", e);
            }
        }
    }
}