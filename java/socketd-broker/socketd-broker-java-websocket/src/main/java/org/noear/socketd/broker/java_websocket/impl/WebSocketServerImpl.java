package org.noear.socketd.broker.java_websocket.impl;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.noear.socketd.broker.java_websocket.WsBioServer;
import org.noear.socketd.core.Channel;
import org.noear.socketd.core.Frame;
import org.noear.socketd.core.impl.ChannelDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.0
 */
public class WebSocketServerImpl extends WebSocketServer {
    static final Logger log = LoggerFactory.getLogger(WebSocketServerImpl.class);

    private WsBioServer server;

    public WebSocketServerImpl(int port, WsBioServer server) {
        super(new InetSocketAddress(port));
        this.server = server;
    }

    public WebSocketServerImpl(String addr, int port, WsBioServer server) {
        super(new InetSocketAddress(addr, port));
        this.server = server;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Channel channel = new ChannelDefault<>(conn, server.config(), server.assistant());
        conn.setAttachment(channel);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Channel channel = conn.getAttachment();
        server.processor().onClose(channel.getSession());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        //sockted nonsupport
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        try {
            Channel channel = conn.getAttachment();
            Frame frame = server.assistant().read(message);
            server.processor().onReceive(channel, frame);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        try {
            Channel channel = conn.getAttachment();
            server.processor().onError(channel.getSession(), ex);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onStart() {
        log.info("Server:Websocket onStart...");
    }
}
