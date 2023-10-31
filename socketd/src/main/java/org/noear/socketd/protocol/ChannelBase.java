package org.noear.socketd.protocol;

import java.io.IOException;

/**
 * 通道基类
 *
 * @author noear
 * @since 2.0
 */
public abstract class ChannelBase implements Channel {

    private Handshaker handshaker;
    private long liveTime;
    //
    @Override
    public void setHandshaker(Handshaker handshaker) {
        this.handshaker = handshaker;
    }


    @Override
    public Handshaker getHandshaker() {
        return handshaker;
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
        send(Frames.connectFrame(uri), null);
    }

    @Override
    public void sendConnack(Message connect) throws IOException {
        send(Frames.connackFrame(connect), null);
    }

    @Override
    public void sendPing() throws IOException {
        send(Frames.pingFrame(), null);
    }

    @Override
    public void sendPong() throws IOException {
        send(Frames.pongFrame(), null);
    }
}
