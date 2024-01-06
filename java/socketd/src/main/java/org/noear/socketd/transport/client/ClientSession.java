package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Consumer;

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
     * @return 流
     */
    default StreamSend send(String event, Entity content) throws IOException {
        return send(event, content, null);
    }

    /**
     * 发送
     *
     * @param event   事件
     * @param content 内容
     * @return 流
     */
    StreamSend send(String event, Entity content, Consumer<StreamSend> consumer) throws IOException;

    /**
     * 发送并请求
     *
     * @param event   事件
     * @param content 内容
     * @return 流
     */
    default StreamRequest sendAndRequest(String event, Entity content) throws IOException {
        return sendAndRequest(event, content, 0, null);
    }

    /**
     * 发送并请求
     *
     * @param event   事件
     * @param content 内容
     * @return 流
     */
    default StreamRequest sendAndRequest(String event, Entity content, Consumer<StreamRequest> consumer) throws IOException {
        return sendAndRequest(event, content, 0, consumer);
    }

    /**
     * 发送并请求（限为一次答复；指定超时）
     *
     * @param event   事件
     * @param content 内容
     * @param timeout 超时（毫秒）
     * @return 流
     */
    default StreamRequest sendAndRequest(String event, Entity content, long timeout) throws IOException {
        return sendAndRequest(event, content, timeout, null);
    }

    /**
     * 发送并请求（限为一次答复；指定超时）
     *
     * @param event   事件
     * @param content 内容
     * @param timeout 超时（毫秒）
     * @return 流
     */
    StreamRequest sendAndRequest(String event, Entity content, long timeout, Consumer<StreamRequest> consumer) throws IOException;

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event   事件
     * @param content 内容
     * @return 流
     */
    default StreamSubscribe sendAndSubscribe(String event, Entity content) throws IOException {
        return sendAndSubscribe(event, content, 0, null);
    }

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event   事件
     * @param content 内容
     * @return 流
     */
    default StreamSubscribe sendAndSubscribe(String event, Entity content, Consumer<StreamSubscribe> consumer) throws IOException {
        return sendAndSubscribe(event, content, 0, consumer);
    }

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event   事件
     * @param content 内容
     * @param timeout 超时（毫秒）
     * @return 流
     */
    default StreamSubscribe sendAndSubscribe(String event, Entity content, long timeout) throws IOException {
        return sendAndSubscribe(event, content, timeout, null);
    }


    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event   事件
     * @param content 内容
     * @param timeout 超时（毫秒）
     * @return 流
     */
    StreamSubscribe sendAndSubscribe(String event, Entity content, long timeout, Consumer<StreamSubscribe> consumer) throws IOException;
}
