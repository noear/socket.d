package org.noear.socketd.transport.core.internal;

import org.noear.socketd.exception.SocketdChannelException;
import org.noear.socketd.exception.SocketdException;
import org.noear.socketd.exception.SocketdTimeoutException;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.utils.IoConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class SessionDefault extends SessionBase {
    private static final Logger log = LoggerFactory.getLogger(SessionDefault.class);
    private String pathNew;

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
    public Handshake getHandshake() {
        return channel.getHandshake();
    }

    /**
     * 获取握手参数
     *
     * @param name 名字
     */
    public String getParam(String name) {
        return getHandshake().getParam(name);
    }

    /**
     * 获取握手参数或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    public String getParamOrDefault(String name, String def) {
        return getHandshake().getParamOrDefault(name, def);
    }

    /**
     * 获取路径
     */
    @Override
    public String getPath() {
        if (pathNew == null) {
            return getHandshake().getPath();
        } else {
            return pathNew;
        }
    }

    /**
     * 设置新路径
     */
    @Override
    public void setPathNew(String pathNew) {
        this.pathNew = pathNew;
    }

    /**
     * 手动重连（一般是自动）
     */
    @Override
    public void reconnect() throws Exception {
        channel.reconnect();
    }

    /**
     * 手动发送 Ping（一般是自动）
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
        Message message = new MessageDefault().sid(generateId()).topic(topic).entity(content);

        channel.send(new Frame(Flag.Message, message), null);
    }

    /**
     * 发送并请求
     *
     * @param topic   主题
     * @param content 内容
     */
    @Override
    public Entity sendAndRequest(String topic, Entity content) throws IOException {
        return sendAndRequest(topic, content, channel.getConfig().getReplyTimeout());
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
        if (timeout < 100) {
            timeout = channel.getConfig().getReplyTimeout();
        }

        //背压控制
        if (channel.getRequests().get() > channel.getConfig().getMaxRequests()) {
            throw new SocketdException("Sending too many requests: " + channel.getRequests().get());
        } else {
            channel.getRequests().incrementAndGet();
        }

        Message message = new MessageDefault().sid(generateId()).topic(topic).entity(content);

        try {
            CompletableFuture<Entity> future = new CompletableFuture<>();
            channel.send(new Frame(Flag.Request, message), new AcceptorRequest(future, timeout));

            try {
                return future.get(timeout, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                StringBuilder hint = new StringBuilder();
                hint.append(", sessionId=").append(channel.getSession().getSessionId());
                hint.append(", topic=").append(topic);
                hint.append(", sid=").append(message.getSid());

                if (channel.isValid()) {
                    throw new SocketdTimeoutException("Request reply timeout>" + timeout + hint);
                } else {
                    throw new SocketdChannelException("This channel is closed" + hint);
                }
            } catch (Throwable e) {
                throw new SocketdException(e);
            }
        } finally {
            channel.removeAcceptor(message.getSid());
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
    public void sendAndSubscribe(String topic, Entity content, IoConsumer<Entity> consumer) throws IOException {
        Message message = new MessageDefault().sid(generateId()).topic(topic).entity(content);
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
        channel.send(new Frame(Flag.Reply, new MessageDefault().sid(from.getSid()).entity(content)), null);
    }

    /**
     * 答复并结束（即最后一次答复）
     *
     * @param from    来源消息
     * @param content 内容
     */
    @Override
    public void replyEnd(Message from, Entity content) throws IOException {
        channel.send(new Frame(Flag.ReplyEnd, new MessageDefault().sid(from.getSid()).entity(content)), null);
    }

    /**
     * 关闭
     */
    @Override
    public void close() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("The session will be closed, sessionId={}", getSessionId());
        }

        if (channel.isValid()) {
            try {
                channel.sendClose();
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("{}", e);
                }
            }
        }

        channel.close();
    }
}