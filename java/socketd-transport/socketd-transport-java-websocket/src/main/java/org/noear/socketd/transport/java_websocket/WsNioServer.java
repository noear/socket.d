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
public class WsNioServer extends ServerBase<WsNioChannelAssistant> {
    private static final Logger log = LoggerFactory.getLogger(WsNioServer.class);
    private WebSocketServerImpl server;

    public WsNioServer(ServerConfig config) {
        super(config, new WsNioChannelAssistant(config));
    }

    @Override
    public Server start() throws IOException {
        if (isStarted) {
            throw new IllegalStateException("Server started");
        }else {
            isStarted = true;
        }

        if (config().getHost() != null) {
            server = new WebSocketServerImpl(config().getPort(), this);
        } else {
            server = new WebSocketServerImpl(config().getHost(), config().getPort(), this);
        }

        //支持 ssl
        if (config().getSslContext() != null) {
            server.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(config().getSslContext()));
        }

        //闲置超时
        if(config().clientMode() == false && config().getIdleTimeout() > 0L) {
            //单位：秒
            server.setConnectionLostTimeout((int) (config().getIdleTimeout() / 1000L));
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
            server.stop();
        } catch (Exception e) {
            log.debug("{}", e);
        }
    }
}