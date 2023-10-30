package org.noear.socketd.broker.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.Frame;
import org.noear.socketd.protocol.impl.ChannelDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.0
 */
public class SocketServerImpl extends WebSocketServer {
    static final Logger log = LoggerFactory.getLogger(SocketServerImpl.class);

    private WsBioServer server;

    public SocketServerImpl(int port, WsBioServer server) {
        super(new InetSocketAddress(port));
        this.server = server;
    }

    public SocketServerImpl(String addr, int port, WsBioServer server) {
        super(new InetSocketAddress(addr, port));
        this.server = server;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        log.info("Server:Websocket onOpen...");

        Channel channel = new ChannelDefault<>(conn, conn::close, server.exchanger());
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
            Frame frame = server.exchanger().read(message);
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
