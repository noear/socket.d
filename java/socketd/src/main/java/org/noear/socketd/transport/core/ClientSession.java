package org.noear.socketd.transport.core;

import org.noear.socketd.utils.IoConsumer;

import java.io.Closeable;
import java.io.IOException;

/**
 * 客户会话
 *
 * @author noear
 */
public interface ClientSession extends Closeable {
    /**
     * 是否有效
     */
    boolean isValid();

    /**
     * 手动重连（一般是自动）
     */
    void reconnect() throws IOException;

    /**
     * 发送
     *
     * @param event   事件
     * @param content 内容
     */
    void send(String event, Entity content) throws IOException;

    /**
     * 发送并请求
     *
     * @param event   事件
     * @param content 内容
     */
    Entity sendAndRequest(String event, Entity content) throws IOException;

    /**
     * 发送并请求（限为一次答复；指定超时）
     *
     * @param event   事件
     * @param content 内容
     * @param timeout 超时（毫秒）
     */
    Entity sendAndRequest(String event, Entity content, long timeout) throws IOException;

    /**
     * 发送并请求（限为一次答复；指定回调）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     */
    void sendAndRequest(String event, Entity content, IoConsumer<Entity> consumer) throws IOException;

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     */
    void sendAndSubscribe(String event, Entity content, IoConsumer<Entity> consumer) throws IOException;

}
