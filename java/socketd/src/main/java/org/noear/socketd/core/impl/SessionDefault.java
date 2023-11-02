package org.noear.socketd.core.impl;

import org.noear.socketd.exception.SocketdException;
import org.noear.socketd.exception.SocketdTimeoutException;
import org.noear.socketd.core.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * 会话默认实现
 *
 * @author noear
 * @since 2.0
 */
public class SessionDefault extends SessionBase implements Session {
    public SessionDefault(Channel channel) {
        super(channel);
    }

    /**
     * 是否有效
     */
    @Override
    public boolean isValid() {
        return channel.isValid();
    }

    /**
     * 获取远程地址
     */
    @Override
    public InetSocketAddress getRemoteAddress() throws IOException {
        return channel.getRemoteAddress();
    }

    /**
     * 获取本地地址
     */
    @Override
    public InetSocketAddress getLocalAddress() throws IOException {
        return channel.getLocalAddress();
    }

    /**
     * 获取握手信息
     */
    @Override
    public Handshaker getHandshaker() {
        return channel.getHandshaker();
    }

    /**
     * 发送 Ping
     */
    @Override
    public void sendPing() throws IOException {
        channel.sendPing();
    }

    /**
     * 发送
     */
    @Override
    public void send(String topic, Entity content) throws IOException {
        Message message = new MessageDefault().key(generateKey()).topic(topic).entity(content);

        channel.send(new Frame(Flag.Message, message), null);
    }


    /**
     * 发送并请求（限为一次答复；指定超时）
     *
     * @param topic   主题
     * @param content 内容
     * @param timeout 超时（毫秒）
     */
    @Override
    public Entity sendAndRequest(String topic, Entity content, long timeout) throws IOException {
        //背压控制
        if (channel.getRequests().get() > channel.getConfig().getMaxRequests()) {
            throw new SocketdException("Sending too many requests: " + channel.getRequests().get());
        } else {
            channel.getRequests().incrementAndGet();
        }

        try {

            Message message = new MessageDefault().key(generateKey()).topic(topic).entity(content);

            CompletableFuture<Entity> future = new CompletableFuture<>();
            channel.send(new Frame(Flag.Request, message), new AcceptorRequest(future));
            try {
                return future.get(timeout, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                throw new SocketdTimeoutException("Request timeout: " + topic);
            } catch (Throwable e) {
                throw new SocketdException(e);
            }
        } finally {
            channel.getRequests().decrementAndGet();
        }
    }

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param topic    主题
     * @param content  内容
     * @param consumer 回调消费者
     */
    @Override
    public void sendAndSubscribe(String topic, Entity content, Consumer<Entity> consumer) throws IOException {
        Message message = new MessageDefault().key(generateKey()).topic(topic).entity(content);
        channel.send(new Frame(Flag.Subscribe, message), new AcceptorSubscribe(consumer));
    }

    /**
     * 答复
     *
     * @param from    来源消息
     * @param content 内容
     */
    @Override
    public void reply(Message from, Entity content) throws IOException {
        channel.send(new Frame(Flag.Reply, new MessageDefault().key(from.getKey()).entity(content)), null);
    }

    /**
     * 答复并结束（即最后一次答复）
     *
     * @param from    来源消息
     * @param content 内容
     */
    @Override
    public void replyEnd(Message from, Entity content) throws IOException {
        channel.send(new Frame(Flag.ReplyEnd, new MessageDefault().key(from.getKey()).entity(content)), null);
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
