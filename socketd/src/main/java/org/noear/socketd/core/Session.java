package org.noear.socketd.core;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
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
     * 获取所有属性
     */
    Map<String,Object> getAttrMap();

    /**
     * 获取属性
     *
     * @param name 名字
     */
    <T> T getAttr(String name);

    /**
     * 设置属性
     *
     * @param name  名字
     * @param value 值
     */
    <T> void setAttr(String name, T value);

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
     * 答复并结束
     *
     * @param from 来源消息
     */
    void replyEnd(Message from, Entity content) throws IOException;
}