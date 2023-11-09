package org.noear.socketd.transport.java_websocket.impl;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.noear.socketd.transport.java_websocket.WsBioServer;
import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.internal.ChannelDefault;
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
        server.processor().onClose(channel);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if (log.isDebugEnabled()) {
            log.debug("Unsupported onMessage(String test)");
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        try {
            Channel channel = conn.getAttachment();
            Frame frame = server.assistant().read(message);

            if(frame != null) {
                server.processor().onReceive(channel, frame);
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        try {
            Channel channel = conn.getAttachment();

            if (channel != null) {
                //有可能未 onOpen，就 onError 了；此时通道未成
                server.processor().onError(channel, ex);
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onStart() {

    }
}
