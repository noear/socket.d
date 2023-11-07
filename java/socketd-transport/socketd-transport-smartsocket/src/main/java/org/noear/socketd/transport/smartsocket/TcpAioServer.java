package org.noear.socketd.transport.smartsocket;

import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.transport.smartsocket.impl.ServerMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.socket.extension.plugins.IdleStatePlugin;
import org.smartboot.socket.extension.plugins.SslPlugin;
import org.smartboot.socket.transport.AioQuickServer;

/**
 * Tcp-Aio 服务端实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class TcpAioServer extends ServerBase<TcpAioChannelAssistant> {
    private static final Logger log = LoggerFactory.getLogger(TcpAioServer.class);

    private AioQuickServer server;

    public TcpAioServer(ServerConfig config) {
        super(config, new TcpAioChannelAssistant(config));
    }

    @Override
    public Server start() throws Exception {
        if (isStarted) {
            throw new IllegalStateException("Server started");
        }else {
            isStarted = true;
        }

        ServerMessageProcessor processor = new ServerMessageProcessor(this);

        //支持 ssl
        if(config().getSslContext() != null) {
            SslPlugin<Frame> sslPlugin = new SslPlugin<>(config()::getSslContext, sslEngine -> {
                sslEngine.setUseClientMode(false);
            });
            processor.addPlugin(sslPlugin);
        }

        if(config().getIdleTimeout() > 0){
            processor.addPlugin(new IdleStatePlugin<>((int)config().getIdleTimeout(), true, false));
        }

        if (config().getHost() != null) {
            server = new AioQuickServer(config().getPort(),
                    assistant(), processor);
        } else {
            server = new AioQuickServer(config().getHost(), config().getPort(),
                    assistant(), processor);
        }


        server.setThreadNum(config().getCoreThreads());
        server.setBannerEnabled(false);
        if (config().getReadBufferSize() > 0) {
            server.setReadBufferSize(config().getReadBufferSize());
        }
        if (config().getWriteBufferSize() > 0) {
            server.setWriteBuffer(config().getWriteBufferSize(), 16);
        }
        server.start();

        log.info("Server started: {server=" + config().getLocalUrl() + "}");

        return this;
    }

    @Override
    public void stop() {
        if (isStarted) {
            isStarted = false;
        } else {
            return;
        }

        try {
            server.shutdown();
        } catch (Exception e) {
            log.debug("{}", e);
        }
    }
}