package org.noear.socketd;

import java.util.function.Consumer;

/**
 * 会话
 *
 * @author noear
 * @since 2.0
 */
public interface Session {
    /**
     * 获取附件
     *
     * @param key 关键字
     */
    <T> T getAttachment(String key);

    /**
     * 设置附件
     *
     * @param key   关键字
     * @param value 值
     */
    <T> void setAttachment(String key, T value);

    /**
     * 发送
     */
    void send(Message message);

    /**
     * 发送并请求
     */
    Message sendAndRequest(Message message);

    /**
     * 发送并订阅
     */
    void sendAndSubscribe(Message message, Consumer<Message> subscriber);
}
