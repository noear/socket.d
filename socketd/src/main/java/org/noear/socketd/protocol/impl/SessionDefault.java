package org.noear.socketd.protocol.impl;

import org.noear.socketd.protocol.*;
import org.noear.socketd.utils.Utils;

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
    public void send(String topic, Entity content) throws IOException {
        Payload payload = new PayloadDefault().key(Utils.guid()).topic(topic).entity(content);

        channel.send(new Frame(Flag.Message, payload), null);
    }

    @Override
    public Entity sendAndRequest(String topic, Entity content) throws IOException {
        Payload payload = new PayloadDefault().key(Utils.guid()).topic(topic).entity(content);

        CompletableFuture<Entity> future = new CompletableFuture<>();
        channel.send(new Frame(Flag.Request, payload), new AcceptorRequest(future));
        try {
            return future.get(2000, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            throw new IOException(e);
        }
    }

    @Override
    public void sendAndSubscribe(String topic, Entity content, Consumer<Entity> consumer) throws IOException {
        Payload payload = new PayloadDefault().key(Utils.guid()).topic(topic).entity(content);
        channel.send(new Frame(Flag.Subscribe, payload), new AcceptorSubscribe(consumer));
    }

    @Override
    public void reply(Payload from, Entity content) throws IOException {
        channel.send(new Frame(Flag.Reply, new PayloadDefault().key(from.getKey()).entity(content)), null);
    }
}
