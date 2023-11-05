package org.noear.socketd.transport.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 通道基类
 *
 * @author noear
 * @since 2.0
 */
public abstract class ChannelBase implements Channel {
    //最大请求数（根据请求、响应加减计数）
    private final Config config;

    private final AtomicInteger requests = new AtomicInteger();
    private Handshake handshake;
    private long liveTime;
    private Map<String,Object> attachments;

    public Config getConfig() {
        return config;
    }

    public ChannelBase(Config config) {
        this.config = config;
    }


    @Override
    public <T> T getAttachment(String name) {
        if (attachments == null) {
            return null;
        }
        return (T) attachments.get(name);
    }

    @Override
    public void setAttachment(String name, Object val) {
        if (attachments == null) {
            attachments = new HashMap<>();
        }

        attachments.put(name, val);
    }

    @Override
    public AtomicInteger getRequests() {
        return requests;
    }

    @Override
    public void setHandshake(Handshake handshake) {
        this.handshake = handshake;
    }


    @Override
    public Handshake getHandshake() {
        return handshake;
    }

    @Override
    public void setLiveTime() {
        liveTime = System.currentTimeMillis();
    }

    @Override
    public long getLiveTime() {
        return liveTime;
    }

    @Override
    public void sendConnect(String uri) throws IOException {
        send(Frames.connectFrame(getConfig().getIdGenerator().generate(), uri), null);
    }

    @Override
    public void sendConnack(Message connectMessage) throws IOException {
        send(Frames.connackFrame(connectMessage), null);
    }

    @Override
    public void sendPing() throws IOException {
        send(Frames.pingFrame(), null);
    }

    @Override
    public void sendPong() throws IOException {
        send(Frames.pongFrame(), null);
    }

    @Override
    public void sendClose() throws IOException {
        send(Frames.closeFrame(), null);
    }
}
