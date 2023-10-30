package org.noear.socketd.broker.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.Entity;
import org.noear.socketd.protocol.Frame;
import org.noear.socketd.protocol.impl.ChannelDefault;
import org.noear.socketd.protocol.impl.PayloadDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.ByteBuffer;

public class SocketClientImpl extends WebSocketClient {
    static final Logger log = LoggerFactory.getLogger(SocketClientImpl.class);
    private WsClient client;
    private Channel channel;

    public SocketClientImpl(URI serverUri, WsClient client) {
        super(serverUri);
        this.client = client;
        this.channel = new ChannelDefault<>(this,this::close,client.exchanger());
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        client.processor().onOpen(channel.getSession());
    }

    @Override
    public void onMessage(String test) {
        try {
            client.processor().onMessage(channel.getSession(), new PayloadDefault().entity(new Entity(test)));
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        try {
            Frame frame = client.exchanger().read(bytes);
            client.processor().onReceive(channel, frame);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        client.processor().onClose(channel.getSession());
    }

    @Override
    public void onError(Exception e) {
        client.processor().onError(channel.getSession(), e);
    }
}
