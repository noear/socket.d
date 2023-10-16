package org.noear.socketd.protocol;

import org.noear.socketd.Session;

import java.io.IOException;

/**
 * @author noear
 */
public class ChannelBase<S> implements Channel<S> {
    ChannelExchanger<S> exchanger;
    S source;

    public ChannelBase(S source, ChannelExchanger<S> exchanger) {
        this.source = source;
        this.exchanger = exchanger;
    }


    @Override
    public void sendConnect(String uri) throws IOException {
        send(new Frame(Flag.Connect, new Payload("", uri, "", new byte[]{})));
    }

    @Override
    public void sendConnack() throws IOException {
        send(Frames.pingConnack);
    }

    @Override
    public void sendPing() throws IOException {
        send(Frames.pingFrame);
    }

    @Override
    public void sendPong() throws IOException {
        send(Frames.pongFrame);
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
        return null;
    }

    @Override
    public Session getSession() {
        return null;
    }
}
