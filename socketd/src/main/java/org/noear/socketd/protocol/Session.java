package org.noear.socketd.protocol;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * 会话
 *
 * @author noear
 * @since 2.0
 */
public interface Session {
    /**
     * 是否有效
     * */
    boolean isValid();

    /**
     * 获取握手信息
     * */
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
    Entity sendAndRequest(String topic, Entity content) throws IOException;

    /**
     * 发送并订阅
     */
    void sendAndSubscribe(String topic, Entity content, Consumer<Entity> consumer) throws IOException;

    /**
     * 答复
     */
    void reply(Message from, Entity content) throws IOException;
}
