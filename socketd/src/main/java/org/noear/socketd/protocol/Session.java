package org.noear.socketd.protocol;

import org.noear.socketd.protocol.entity.MetaEntity;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

/**
 * 会话
 *
 * @author noear
 * @since 2.0
 */
public interface Session extends Closeable {
    /**
     * 是否有效
     */
    boolean isValid();

    /**
     * 获取远程地址
     */
    InetSocketAddress getRemoteAddress() throws IOException;

    /**
     * 获取本地地址
     */
    InetSocketAddress getLocalAddress() throws IOException;

    /**
     * 获取握手信息
     */
    Handshaker getHandshaker();

    /**
     * 获取附件
     *
     * @param key 关键字
     */
    <T> T getAttachment(Class<T> key);

    /**
     * 设置附件
     *
     * @param key   关键字
     * @param value 值
     */
    <T> void setAttachment(Class<T> key, T value);

    /**
     * 获取会话Id
     */
    String getSessionId();

    /**
     * 发送 Ping
     */
    void sendPing() throws IOException;

    /**
     * 发送
     */
    void send(String topic, Entity content) throws IOException;

    /**
     * 发送并请求
     */
    default Entity sendAndRequest(String topic, Entity content) throws IOException {
        return sendAndRequest(topic, content, 3000);
    }

    /**
     * 发送并请求（限为一次答复）
     */
    Entity sendAndRequest(String topic, Entity content, long timeout) throws IOException;

    /**
     * 发送并订阅（不限答复次数）
     */
    void sendAndSubscribe(String topic, Entity content, Consumer<Entity> consumer) throws IOException;

    /**
     * 答复
     *
     * @param from 来源消息
     */
    void reply(Message from, Entity content) throws IOException;

    /**
     * 答复为空（快速完成答复链）
     *
     * @param from 来源消息
     */
    default void replyAsEmpty(Message from) throws IOException {
        reply(from, new MetaEntity(""));
    }
}