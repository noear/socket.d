package org.noear.socketd.broker.java_websocket;

import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.noear.socketd.broker.java_websocket.impl.WebSocketServerImpl;
import org.noear.socketd.server.ServerBase;
import org.noear.socketd.server.ServerConfig;

import java.io.IOException;

/**
 * Ws-Bio 服务端实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class WsBioServer extends ServerBase<WsBioExchanger> {
    private WebSocketServerImpl server;

    public WsBioServer(ServerConfig serverConfig) {
        super(serverConfig, new WsBioExchanger());
    }

    @Override
    public void start() throws IOException {
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
    }

    @Override
    public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }
}