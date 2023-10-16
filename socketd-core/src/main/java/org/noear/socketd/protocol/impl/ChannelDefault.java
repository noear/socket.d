package org.noear.socketd.protocol.impl;

import org.noear.socketd.protocol.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 2.0
 */
public class ChannelDefault<S extends Closeable> implements Channel {
    private S source;
    private ChannelExchanger<S> exchanger;
    private Map<String, Object> attachments;
    private Handshaker handshaker;
    private Session session;

    public ChannelDefault(S source, ChannelExchanger<S> exchanger) {
        this.source = source;
        this.exchanger = exchanger;
        this.attachments = new HashMap<>();
    }


    @Override
    public <T> T getAttachment(String key) {
        return (T) attachments.get(key);
    }

    @Override
    public <T> void setAttachment(String key, T value) {
        attachments.put(key, value);
    }

    @Override
    public void setHandshaker(Handshaker handshaker) {
        this.handshaker = handshaker;
    }

    @Override
    public void sendConnect(String uri) throws IOException {
        send(FrameFactory.connectFrame(uri));
    }

    @Override
    public void sendConnack() throws IOException {
        send(FrameFactory.connackFrame());
    }

    @Override
    public void sendPing() throws IOException {
        send(FrameFactory.pingFrame());
    }

    @Override
    public void sendPong() throws IOException {
        send(FrameFactory.pongFrame());
    }

    @Override
    public void send(Frame frame) throws IOException {
        exchanger.write(source, frame);
    }

    @Override
    public Frame receive() throws IOException {
        return exchanger.read(source);
    }

    @Override
    public Handshaker getHandshaker() {
        return handshaker;
    }

    @Override
    public Session getSession() {
        if (session == null) {
            session = new SessionDefault(this);
        }

        return session;
    }

    @Override
    public void close() throws IOException {
        source.close();
    }
}
