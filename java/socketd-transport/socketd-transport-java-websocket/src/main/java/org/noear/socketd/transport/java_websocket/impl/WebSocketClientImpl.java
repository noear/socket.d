package org.noear.socketd.transport.java_websocket.impl;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.noear.socketd.transport.core.Flag;
import org.noear.socketd.transport.java_websocket.WsBioClient;
import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.impl.ChannelDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class WebSocketClientImpl extends WebSocketClient {
    static final Logger log = LoggerFactory.getLogger(WebSocketClientImpl.class);
    private WsBioClient client;
    private Channel channel;
    private CompletableFuture<Channel> futureChannel;

    public WebSocketClientImpl(URI serverUri, WsBioClient client) {
        super(serverUri);
        this.client = client;
        this.channel = new ChannelDefault<>(this, client.config(), client.assistant());
    }

    public CompletableFuture<Channel> getChannel() {
        return futureChannel;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        try {
            channel.sendConnect(client.config().getUrl());
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void connect() {
        this.futureChannel = new CompletableFuture<>();
        super.connect();
    }

    @Override
    public void reconnect() {
        this.futureChannel = new CompletableFuture<>();
        super.reconnect();
    }

    @Override
    public void onMessage(String test) {
        //sockted nonsupport
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        try {
            Frame frame = client.assistant().read(bytes);

            if (frame != null) {
                client.processor().onReceive(channel, frame);

                if(frame.getFlag() == Flag.Connack){
                    futureChannel.complete(channel);
                }
            }
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
