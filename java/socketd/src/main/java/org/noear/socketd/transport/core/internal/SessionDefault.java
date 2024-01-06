package org.noear.socketd.transport.core.internal;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.stream.StreamSendImpl;
import org.noear.socketd.transport.core.stream.StreamRequestImpl;
import org.noear.socketd.transport.core.stream.StreamSubscribeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
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
    public InetSocketAddress remoteAddress() throws IOException {
        return channel.getRemoteAddress();
    }

    /**
     * 获取本地地址
     */
    @Override
    public InetSocketAddress localAddress() throws IOException {
        return channel.getLocalAddress();
    }

    /**
     * 获取握手信息
     */
    @Override
    public Handshake handshake() {
        return channel.getHandshake();
    }

    /**
     * 获取握手参数
     *
     * @param name 名字
     */
    public String param(String name) {
        return handshake().param(name);
    }

    /**
     * 获取握手参数或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    public String paramOrDefault(String name, String def) {
        return handshake().paramOrDefault(name, def);
    }

    /**
     * 获取路径
     */
    @Override
    public String path() {
        if (pathNew == null) {
            return handshake().uri().getPath();
        } else {
            return pathNew;
        }
    }

    /**
     * 设置新路径
     */
    @Override
    public void pathNew(String pathNew) {
        this.pathNew = pathNew;
    }

    /**
     * 手动重连（一般是自动）
     */
    @Override
    public void reconnect() throws IOException {
        channel.reconnect();
    }

    /**
     * 手动发送 Ping（一般是自动）
     */
    @Override
    public void sendPing() throws IOException {
        channel.sendPing();
    }

    @Override
    public void sendAlarm(Message from, String alarm) throws IOException {
        channel.sendAlarm(from, alarm);
    }

    /**
     * 发送
     *
     * @param event   事件
     * @param content 内容
     */
    @Override
    public StreamSend send(String event, Entity content, Consumer<StreamSend> consumer) throws IOException {
        MessageInternal message = new MessageBuilder()
                .sid(generateId())
                .event(event)
                .entity(content)
                .build();

        StreamSendImpl stream = new StreamSendImpl(message.sid());
        if (consumer != null) {
            consumer.accept(stream);
        }
        channel.send(new Frame(Flags.Message, message), stream);
        return stream;
    }

    /**
     * 发送并请求（限为一次答复；指定超时）
     *
     * @param event   事件
     * @param content 内容
     * @param timeout 超时（毫秒）
     */
    @Override
    public StreamRequest sendAndRequest(String event, Entity content, long timeout, Consumer<StreamRequest> consumer) throws IOException {
        if (timeout < 10) {
            timeout = channel.getConfig().getRequestTimeout();
        }

        MessageInternal message = new MessageBuilder()
                .sid(generateId())
                .event(event)
                .entity(content)
                .build();

        StreamRequestImpl stream = new StreamRequestImpl(message.sid(), timeout);
        if (consumer != null) {
            consumer.accept(stream);
        }
        channel.send(new Frame(Flags.Request, message), stream);
        return stream;
    }


    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event   事件
     * @param content 内容
     * @param timeout 超时
     */
    @Override
    public StreamSubscribe sendAndSubscribe(String event, Entity content, long timeout, Consumer<StreamSubscribe> consumer) throws IOException {
        MessageInternal message = new MessageBuilder()
                .sid(generateId())
                .event(event)
                .entity(content)
                .build();

        StreamSubscribeImpl stream = new StreamSubscribeImpl(message.sid(), timeout);
        if (consumer != null) {
            consumer.accept(stream);
        }
        channel.send(new Frame(Flags.Subscribe, message), stream);
        return stream;
    }

    /**
     * 答复
     *
     * @param from    来源消息
     * @param content 内容
     */
    @Override
    public void reply(Message from, Entity content) throws IOException {
        MessageInternal message = new MessageBuilder()
                .sid(from.sid())
                .event(from.event())
                .entity(content)
                .build();

        channel.send(new Frame(Flags.Reply, message), null);
    }

    /**
     * 答复并结束（即最后一次答复）
     *
     * @param from    来源消息
     * @param content 内容
     */
    @Override
    public void replyEnd(Message from, Entity content) throws IOException {
        MessageInternal message = new MessageBuilder()
                .sid(from.sid())
                .event(from.event())
                .entity(content)
                .build();

        channel.send(new Frame(Flags.ReplyEnd, message), null);
    }

    /**
     * 关闭
     */
    @Override
    public void close() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("{} session will be closed, sessionId={}",
                    channel.getConfig().getRoleName(), sessionId());
        }

        if (channel.isValid()) {
            try {
                channel.sendClose();
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("{} channel sendClose error",
                            channel.getConfig().getRoleName(), e);
                }
            }
        }

        channel.close(Constants.CLOSE4_USER);
    }
}