package org.noear.socketd.protocol.impl;

import org.noear.socketd.protocol.*;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * 服务端会话
 *
 * @author noear
 * @since 2.0
 */
public class SessionDefault extends SessionBase implements Session {
    private Channel channel;

    public SessionDefault(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void sendPing() throws IOException {
        channel.sendPing();
    }

    @Override
    public void send(Payload message) throws IOException {
        channel.send(new Frame(Flag.Message, message));
    }

    @Override
    public Payload sendAndRequest(Payload message) throws IOException {
        channel.send(new Frame(Flag.Request, message));
        return null;
    }

    @Override
    public void sendAndSubscribe(Payload message, Consumer<Payload> subscriber) throws IOException {
        channel.send(new Frame(Flag.Subscribe, message));
    }

    @Override
    public void reply(Payload from, Payload message) throws IOException {

    }

}
