package org.noear.socketd.protocol.impl;

import org.noear.socketd.protocol.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
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
        channel.send(new Frame(Flag.Message, message), null);
    }

    @Override
    public Payload sendAndRequest(Payload message) throws IOException {
        CompletableFuture<Payload> future = new CompletableFuture<>();
        channel.send(new Frame(Flag.Request, message), new AcceptorRequest(future));
        try {
            return future.get(2000, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            throw new IOException(e);
        }
    }

    @Override
    public void sendAndSubscribe(Payload message, Consumer<Payload> consumer) throws IOException {
        channel.send(new Frame(Flag.Subscribe, message), new AcceptorSubscribe(consumer));
    }

    @Override
    public void reply(Payload from, byte[] content) throws IOException {
        channel.send(new Frame(Flag.Reply, new PayloadDefault(from.getKey(), "", "", content)), null);
    }
}
