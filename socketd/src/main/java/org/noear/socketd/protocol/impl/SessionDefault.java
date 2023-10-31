package org.noear.socketd.protocol.impl;

import org.noear.socketd.exception.SocktedException;
import org.noear.socketd.exception.SocktedTimeoutException;
import org.noear.socketd.protocol.*;
import org.noear.socketd.utils.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
    public boolean isValid() {
        return channel.isValid();
    }

    @Override
    public InetSocketAddress getRemoteAddress() throws IOException {
        return channel.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() throws IOException {
        return channel.getLocalAddress();
    }

    @Override
    public Handshaker getHandshaker() {
        return channel.getHandshaker();
    }

    @Override
    public void sendPing() throws IOException {
        channel.sendPing();
    }

    @Override
    public void send(String topic, Entity content) throws IOException {
        Message message = new MessageDefault().key(Utils.guid()).topic(topic).entity(content);

        channel.send(new Frame(Flag.Message, message), null);
    }


    @Override
    public Entity sendAndRequest(String topic, Entity content, long timeout) throws IOException {
        //背压控制
        if (channel.getRequests().get() > channel.getRequestMax()) {
            throw new SocktedException("Sending too many requests: " + channel.getRequests().get());
        } else {
            channel.getRequests().incrementAndGet();
        }

        try {

            Message message = new MessageDefault().key(Utils.guid()).topic(topic).entity(content);

            CompletableFuture<Entity> future = new CompletableFuture<>();
            channel.send(new Frame(Flag.Request, message), new AcceptorRequest(future));
            try {
                return future.get(timeout, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                throw new SocktedTimeoutException("Request timeout: " + topic);
            } catch (Throwable e) {
                throw new SocktedException(e);
            }
        } finally {
            channel.getRequests().decrementAndGet();
        }
    }

    @Override
    public void sendAndSubscribe(String topic, Entity content, Consumer<Entity> consumer) throws IOException {
        Message message = new MessageDefault().key(Utils.guid()).topic(topic).entity(content);
        channel.send(new Frame(Flag.Subscribe, message), new AcceptorSubscribe(consumer));
    }

    @Override
    public void reply(Message from, Entity content) throws IOException {
        channel.send(new Frame(Flag.Reply, new MessageDefault().key(from.getKey()).entity(content)), null);
    }
}
