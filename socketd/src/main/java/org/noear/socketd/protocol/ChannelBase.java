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
    public void sendConnect(String uri) throws IOException {
        send(Frames.connectFrame(uri), null);
    }

    @Override
    public void sendConnack() throws IOException {
        send(Frames.connackFrame(), null);
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
