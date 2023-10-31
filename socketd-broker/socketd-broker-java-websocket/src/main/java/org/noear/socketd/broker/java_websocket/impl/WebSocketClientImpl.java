package org.noear.socketd.broker.java_websocket.impl;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.noear.socketd.broker.java_websocket.WsBioClient;
import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.Frame;
import org.noear.socketd.protocol.impl.ChannelDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.ByteBuffer;

public class WebSocketClientImpl extends WebSocketClient {
    static final Logger log = LoggerFactory.getLogger(WebSocketClientImpl.class);
    private WsBioClient client;
    private Channel channel;

    public WebSocketClientImpl(URI serverUri, WsBioClient client) {
        super(serverUri);
        this.client = client;
        this.channel = new ChannelDefault<>(this, client.assistant());
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("Client:Websocket onOpen...");

        try {
            channel.sendConnect(client.config().getUrl());
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onMessage(String test) {
        //sockted nonsupport
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        try {
            Frame frame = client.assistant().read(bytes);
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
