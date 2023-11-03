package org.noear.socketd.transport.java_websocket;

import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.noear.socketd.transport.java_websocket.impl.WebSocketServerImpl;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Ws-Bio 服务端实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class WsBioServer extends ServerBase<WsBioChannelAssistant> {
    private static final Logger log = LoggerFactory.getLogger(WsBioServer.class);
    private WebSocketServerImpl server;

    public WsBioServer(ServerConfig config) {
        super(config, new WsBioChannelAssistant(config));
    }

    @Override
    public Server start() throws IOException {
        if (config().getHost() != null) {
            server = new WebSocketServerImpl(config().getPort(), this);
        } else {
            server = new WebSocketServerImpl(config().getHost(), config().getPort(), this);
        }

        //支持 ssl
        if (config().getSslContext() != null) {
            server.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(config().getSslContext()));
        }

        server.start();

        log.info("Server started: {server=" + config().getLocalUrl() + "}");

        return this;
    }

    @Override
    public void stop() {
        if (server == null) {
            return;
        }
        try {
            server.stop();
        } catch (Exception e) {
            log.debug("{}", e);
        }
    }
}