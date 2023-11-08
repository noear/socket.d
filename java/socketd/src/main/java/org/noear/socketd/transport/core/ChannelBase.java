package org.noear.socketd.transport.core;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    private final Map<String, Object> attachments = new ConcurrentHashMap<>();
    private HandshakeInternal handshake;
    private long liveTime;
    //是否已关闭（用于做关闭异常提醒）
    private boolean isClosed;
    //是否已打开（即 onOpen 通过后）
    private boolean isOpened;

    public Config getConfig() {
        return config;
    }

    public ChannelBase(Config config) {
        this.config = config;
    }


    @Override
    public <T> T getAttachment(String name) {
        return (T) attachments.get(name);
    }

    @Override
    public void setAttachment(String name, Object val) {
        attachments.put(name, val);
    }


    @Override
    public boolean isClosed() {
        return isClosed;
    }

    public boolean isOpened(){
        return isOpened;
    }

    @Override
    public void openConfirm() {
        isOpened = true;
    }

    @Override
    public AtomicInteger getRequests() {
        return requests;
    }

    @Override
    public void setHandshake(HandshakeInternal handshake) {
        this.handshake = handshake;
    }


    @Override
    public HandshakeInternal getHandshake() {
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
    public void sendConnack(Message connectMessage, boolean isPassed) throws IOException {
        send(Frames.connackFrame(connectMessage, isPassed), null);
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

    @Override
    public void close() throws IOException {
        isClosed = true;
        attachments.clear();
    }
}
