package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通道基类
 *
 * @author noear
 * @since 2.0
 */
public abstract class ChannelBase implements Channel {
    //最大请求数（根据请求、响应加减计数）
    private final Config config;
    //附件
    private final Map<String, Object> attachments = new ConcurrentHashMap<>();
    //握手信息
    private HandshakeInternal handshake;


    public ChannelBase(Config config) {
        this.config = config;
    }


    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public <T> T getAttachment(String name) {
        return (T) attachments.get(name);
    }

    @Override
    public void putAttachment(String name, Object val) {
        if (val == null) {
            attachments.remove(name);
        } else {
            attachments.put(name, val);
        }
    }

    @Override
    public void setHandshake(HandshakeInternal handshake) {
        if(handshake != null) {
            this.handshake = handshake;
        }
    }


    @Override
    public HandshakeInternal getHandshake() {
        return handshake;
    }

    @Override
    public void sendConnect(String uri, Map<String, String> metaMap) throws IOException {
        send(Frames.connectFrame(getConfig().genId(), uri, metaMap), null);
    }

    @Override
    public void sendConnack() throws IOException {
        send(Frames.connackFrame(getHandshake()), null);
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
    public void sendClose(int code) throws IOException {
        send(Frames.closeFrame(code), null);
    }

    @Override
    public void sendAlarm(Message from, Entity alarm) throws IOException {
        send(Frames.alarmFrame(from, alarm), null);
    }

    @Override
    public void sendPressure(Message from, Entity pressure) throws IOException {
        send(Frames.pressureFrame(from, pressure), null);
    }

    @Override
    public void close(int code) {
        if (code > Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING) {
            attachments.clear();
        }
    }
}
