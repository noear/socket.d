package org.noear.socketd.transport.smartsocket.tcp;

import org.noear.socketd.SocketD;
import org.noear.socketd.exception.SocketDException;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.transport.smartsocket.tcp.impl.FrameProtocol;
import org.noear.socketd.transport.smartsocket.tcp.impl.IdleStatePluginEx;
import org.noear.socketd.transport.smartsocket.tcp.impl.ServerMessageProcessor;
import org.noear.socketd.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.socket.extension.plugins.SslPlugin;
import org.smartboot.socket.transport.AioQuickServer;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;

/**
 * Tcp-Aio 服务端实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class TcpAioServer extends ServerBase<TcpAioChannelAssistant> implements ChannelSupporter<AioSession> {
    private static final Logger log = LoggerFactory.getLogger(TcpAioServer.class);

    private AioQuickServer server;

    private final FrameProtocol frameProtocol;

    public TcpAioServer(ServerConfig config) {
        super(config, new TcpAioChannelAssistant(config));
        this.frameProtocol = new FrameProtocol(this);
    }

    @Override
    public String getTitle() {
        return "tcp/aio/smart-socket 1.5/" + SocketD.version();
    }

    @Override
    public Server start() throws IOException {
        if (isStarted) {
            throw new IllegalStateException("Socket.D server started");
        } else {
            isStarted = true;
        }

        ServerMessageProcessor messageProcessor = new ServerMessageProcessor(this);

        try {
            //支持 ssl
            if (getConfig().getSslContext() != null) {
                SslPlugin<Frame> sslPlugin = new SslPlugin<>(getConfig()::getSslContext, sslEngine -> {
                    sslEngine.setUseClientMode(false);
                });
                messageProcessor.addPlugin(sslPlugin);
            }

            //闲置超时
            if (getConfig().getIdleTimeout() > 0) {
                messageProcessor.addPlugin(new IdleStatePluginEx<>((int) getConfig().getIdleTimeout(), true, false));
            }

            if (StrUtils.isEmpty(getConfig().getHost())) {
                server = new AioQuickServer(getConfig().getPort(),
                        frameProtocol, messageProcessor);
            } else {
                server = new AioQuickServer(getConfig().getHost(), getConfig().getPort(),
                        frameProtocol, messageProcessor);
            }


            server.setThreadNum(Math.max(getConfig().getCodecThreads(), 2));
            server.setBannerEnabled(false);

            if (getConfig().getReadBufferSize() > 0) {
                server.setReadBufferSize(getConfig().getReadBufferSize());
            }

            if (getConfig().getWriteBufferSize() > 0) {
                server.setWriteBuffer(getConfig().getWriteBufferSize(), 16);
            }

            server.start();
        } catch (Exception e) {
            if (server != null) {
                server.shutdown();
            }

            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new SocketDException("Socket.D server start failed!", e);
            }
        }

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