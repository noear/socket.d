package org.noear.socketd.transport.spring.websocket;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.impl.ChannelDefault;
import org.noear.socketd.transport.core.impl.ProcessorDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.util.*;

/**
 * 转到 Sokcet.D 协议的 WebSocketListener（服务端、客户端，都可用）
 *
 * @author noear
 * @since 2.3
 */
public class ToSocketdWebSocketListener extends BinaryWebSocketHandler implements SubProtocolCapable {
    static final String SOCKETD_KEY = "SOCKETD_KEY";

    static final Logger log = LoggerFactory.getLogger(ToSocketdWebSocketListener.class);

    private final Config config;
    private final InnerChannelAssistant assistant;
    private final Processor processor;
    private final InnerListenerWrapper listenerWrapper;

    private final InnerChannelSupporter supporter;

    public ToSocketdWebSocketListener(Config config) {
        this(config, null);
    }

    public ToSocketdWebSocketListener(Config config, Listener listener) {
        this.config = config;
        this.assistant = new InnerChannelAssistant(config);
        this.listenerWrapper = new InnerListenerWrapper();
        this.processor = new ProcessorDefault();
        this.processor.setListener(listenerWrapper);
        this.supporter = new InnerChannelSupporter(this);

        if (listener == null) {
            if (this instanceof Listener) {
                setListener((Listener) this);
            }
        } else {
            setListener(listener);
        }
    }

    /**
     * 设置 Socket.D 监听器
     */
    public void setListener(Listener listener) {
        this.listenerWrapper.setListener(listener);
    }

    /**
     * 获取 通道
     */
    private ChannelInternal getChannel(WebSocketSession socket) {
        ChannelInternal channel = (ChannelInternal) socket.getAttributes().get(SOCKETD_KEY);

        if (channel == null) {
            channel = new ChannelDefault<>(socket, supporter);
            socket.getAttributes().put(SOCKETD_KEY, channel);
        }

        return channel;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        ChannelInternal channel = getChannel(session);

        //头信息
        Map<String, String> headerMap = new HashMap<>();
        for (Map.Entry<String, List<String>> kv : session.getHandshakeHeaders().entrySet()) {
            headerMap.put(kv.getKey(), String.join(";", kv.getValue()));
        }
        channel.getSession().attrPut(InnerListenerWrapper.WS_HANDSHAKE_HEADER, headerMap);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        try {
            ChannelInternal channel = getChannel(session);
            Frame frame = assistant.read(message.getPayload());

            if (frame != null) {
                processor.reveFrame(channel, frame);
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        try {
            ChannelInternal channel = getChannel(session);
            processor.onClose(channel);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        try {
            ChannelInternal channel = getChannel(session);

            if (channel != null) {
                //有可能未 onOpen，就 onError 了；此时通道未成
                processor.onError(channel, exception);
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        if (assertHandshake(session)) {
            super.handlePongMessage(session, message);
        }
    }

    /**
     * 禁止 ws 客户端连接 sd:ws 服务（避免因为 ws 心跳，又不会触发空闲超时）
     */
    protected boolean assertHandshake(WebSocketSession conn) throws IOException {
        ChannelInternal channel = getChannel(conn);

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

    @Override
    public List<String> getSubProtocols() {
        if (config.isUseSubprotocols()) {
            return Arrays.asList(SocketD.protocolName().toLowerCase());
        } else {
            //支持有子协议，或没子协议
            return Arrays.asList(SocketD.protocolName().toLowerCase(), "");
        }
    }

    private static class InnerChannelSupporter implements ChannelSupporter<WebSocketSession> {
        private ToSocketdWebSocketListener l;

        InnerChannelSupporter(ToSocketdWebSocketListener l) {
            this.l = l;
        }

        @Override
        public Processor getProcessor() {
            return l.processor;
        }

        @Override
        public ChannelAssistant<WebSocketSession> getAssistant() {
            return l.assistant;
        }

        @Override
        public Config getConfig() {
            return l.config;
        }
    }
}