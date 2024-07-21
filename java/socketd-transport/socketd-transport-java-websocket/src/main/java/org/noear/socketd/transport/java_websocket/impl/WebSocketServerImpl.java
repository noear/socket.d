package org.noear.socketd.transport.java_websocket.impl;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.protocols.Protocol;
import org.java_websocket.server.WebSocketServer;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.java_websocket.WsNioServer;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.impl.ChannelDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author noear
 * @since 2.0
 */
public class WebSocketServerImpl extends WebSocketServer {
    static final Logger log = LoggerFactory.getLogger(WebSocketServerImpl.class);
    public static final String WS_HANDSHAKE_HEADER = "ws-handshake-headers";

    private WsNioServer server;

    public WebSocketServerImpl(int port, WsNioServer server) {
        super(new InetSocketAddress(port),
                server.getConfig().getCodecThreads(),
                server.getConfig().isUseSubprotocols() ?
                        Collections.singletonList(new Draft_6455(Collections.emptyList(), Arrays.asList(new Protocol(SocketD.protocolName())))) :
                        //支持有子协议，或没子协议
                        Collections.singletonList(new Draft_6455(Collections.emptyList(), Arrays.asList(new Protocol(SocketD.protocolName()), new Protocol("")))));

        this.server = server;
    }

    public WebSocketServerImpl(String addr, int port, WsNioServer server) {
        super(new InetSocketAddress(addr, port),
                server.getConfig().getCodecThreads(),
                server.getConfig().isUseSubprotocols() ?
                        Collections.singletonList(new Draft_6455(Collections.emptyList(), Collections.singletonList(new Protocol(SocketD.protocolName())))) :
                        null);

        this.server = server;
    }

    @Override
    public void onWebsocketPing(WebSocket conn, Framedata f) {
        if (assertHandshake(conn)) {
            super.onWebsocketPing(conn, f);
        }
    }

    @Override
    public void onWebsocketPong(WebSocket conn, Framedata f) {
        if (assertHandshake(conn)) {
            super.onWebsocketPong(conn, f);
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        ChannelDefault channel = new ChannelDefault<>(conn, server);
        conn.setAttachment(channel);

        //头信息
        Map<String, String> headerMap = new HashMap<>();

        Iterator<String> httpFields = handshake.iterateHttpFields();
        while (httpFields.hasNext()) {
            String name = httpFields.next();
            headerMap.put(name, handshake.getFieldValue(name));
        }

        channel.getSession().attrPut(WS_HANDSHAKE_HEADER, headerMap);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        ChannelInternal channel = conn.getAttachment();
        if (channel != null && channel.getHandshake() != null) {
            server.getProcessor().onClose(channel);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        //文本请求不支持
        conn.close();

        if (log.isWarnEnabled()) {
            log.warn("Server channel unsupported onMessage(String test)");
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        try {
            //用于支持 socket.d 控制 idleTimeout //前端也可能会关闭自动 pingPong
            ((WebSocketImpl) conn).updateLastPong();

            ChannelInternal channel = conn.getAttachment();
            Frame frame = server.getAssistant().read(message);

            if (frame != null) {
                server.getProcessor().reveFrame(channel, frame);
            }
        } catch (Throwable e) {
            log.warn("WebSocket server onMessage error", e);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        try {
            ChannelInternal channel = conn.getAttachment();

            if (channel != null) {
                //有可能未 onOpen，就 onError 了；此时通道未成
                server.getProcessor().onError(channel, ex);
            }
        } catch (Throwable e) {
            log.warn("WebSocket server onError error", e);
        }
    }

    @Override
    public void onStart() {
        //闲置超时
        if (server.getConfig().getIdleTimeout() > 0L) {
            //单位：秒
            setConnectionLostTimeout((int) (server.getConfig().getIdleTimeout() / 1000L));
        } else {
            setConnectionLostTimeout(0);
        }
    }

    /**
     * 禁止 ws 客户端连接 sd:ws 服务（避免因为 ws 心跳，又不会触发空闲超时）
     */
    protected boolean assertHandshake(WebSocket conn) {
        ChannelInternal channel = conn.getAttachment();

        if (channel == null || channel.getHandshake() == null) {
            conn.close();

            if (log.isWarnEnabled()) {
                log.warn("Server channel no handshake onPingPong");
            }
            return false;
        } else {
            return true;
        }
    }
}