package org.noear.socketd.transport.java_websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.java_websocket.impl.WebSocketServerImpl;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Ws-Bio 服务端实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class WsNioServer extends ServerBase<WsNioChannelAssistant> implements ChannelSupporter<WebSocket> {
    private static final Logger log = LoggerFactory.getLogger(WsNioServer.class);
    private WebSocketServerImpl server;

    public WsNioServer(ServerConfig config) {
        super(config, new WsNioChannelAssistant(config));
    }

    @Override
    public String getTitle() {
        return "ws/nio/java-websocket 1.5/" + SocketD.version();
    }

    @Override
    public void onOpen(Session s) throws IOException {
        Map<String, String> headerMap = s.attr(WebSocketServerImpl.WS_HANDSHAKE_HEADER);
        if (headerMap != null) {
            s.handshake().paramMap().putAll(headerMap);
            s.attrDel(WebSocketServerImpl.WS_HANDSHAKE_HEADER);
        }

        super.onOpen(s);
    }

    @Override
    public Server start() throws IOException {
        if (isStarted) {
            throw new IllegalStateException("Socket.D server started");
        } else {
            isStarted = true;
        }

        if (StrUtils.isEmpty(getConfig().getHost())) {
            server = new WebSocketServerImpl(getConfig().getPort(), this);
        } else {
            server = new WebSocketServerImpl(getConfig().getHost(), getConfig().getPort(), this);
        }

        //支持 ssl
        if (getConfig().getSslContext() != null) {
            server.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(getConfig().getSslContext()));
        }

        server.setReuseAddr(true);
        server.start();

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
                server.stop();
            }
        } catch (Exception e) {
            log.debug("Server stop error", e);
        }
    }
}