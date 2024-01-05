package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.*;

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
     * 获取会话Id
     */
    String sessionId();

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
    Stream send(String event, Entity content) throws IOException;

    /**
     * 发送并请求
     *
     * @param event   事件
     * @param content 内容
     */
    default StreamRequest sendAndRequest(String event, Entity content) throws IOException {
        return sendAndRequest(event, content, 0);
    }

    /**
     * 发送并请求（限为一次答复；指定超时）
     *
     * @param event   事件
     * @param content 内容
     * @param timeout 超时（毫秒）
     */
    StreamRequest sendAndRequest(String event, Entity content, long timeout) throws IOException;

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event   事件
     * @param content 内容
     * @return 流
     */
    default StreamSubscribe sendAndSubscribe(String event, Entity content) throws IOException {
        return sendAndSubscribe(event, content, 0);
    }

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event   事件
     * @param content 内容
     * @param timeout 超时（毫秒）
     * @return 流
     */
    StreamSubscribe sendAndSubscribe(String event, Entity content, long timeout) throws IOException;
}
