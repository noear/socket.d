package org.noear.socketd.broker.bio.server;

import org.noear.socketd.Session;
import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.Frame;
import org.noear.socketd.protocol.Frames;
import org.noear.socketd.protocol.Handshaker;
import org.noear.socketd.protocol.codec.EncoderByteBuffer;

import java.io.IOException;
import java.net.Socket;

/**
 * @author noear 2023/10/13 created
 */
public class BioChannel implements Channel {
    private Socket socket;
    private EncoderByteBuffer encoder;
    private BioChannelReceiver receiver;

    public BioChannel(Socket socket) {
        this.socket = socket;
        this.encoder = new EncoderByteBuffer();
        this.receiver = new BioChannelReceiver();
    }

    @Override
    public void sendHandshaked() throws IOException {

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
        socket.getOutputStream().write(encoder.encode(frame).array());
        socket.getOutputStream().flush();
    }

    @Override
    public Frame receive() throws IOException {
        return receiver.receive(socket.getInputStream());
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
