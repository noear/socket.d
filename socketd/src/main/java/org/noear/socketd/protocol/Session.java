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
     * 发送 Ping
     * */
    void sendPing() throws IOException;

    /**
     * 发送
     */
    void send(Payload message) throws IOException;

    /**
     * 发送并请求
     */
    Payload sendAndRequest(Payload message) throws IOException;

    /**
     * 发送并订阅
     */
    void sendAndSubscribe(Payload message, Consumer<Payload> subscriber) throws IOException;

    /**
     * 答复
     * */
    void reply(Payload from, Payload message) throws IOException;
}
