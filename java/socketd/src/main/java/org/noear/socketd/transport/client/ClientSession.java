package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.stream.RequestStream;
import org.noear.socketd.transport.stream.SendStream;
import org.noear.socketd.transport.stream.SubscribeStream;

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
    default SendStream send(String event, Entity content) throws IOException {
        return send(event, content, null);
    }

    /**
     * 发送
     *
     * @param event   事件
     * @param content 内容
     * @return 流
     */
    SendStream send(String event, Entity content, Consumer<SendStream> consumer) throws IOException;

    /**
     * 发送并请求
     *
     * @param event   事件
     * @param content 内容
     * @return 流
     */
    default RequestStream sendAndRequest(String event, Entity content) throws IOException {
        return sendAndRequest(event, content, 0, null);
    }

    /**
     * 发送并请求
     *
     * @param event   事件
     * @param content 内容
     * @return 流
     */
    default RequestStream sendAndRequest(String event, Entity content, Consumer<RequestStream> consumer) throws IOException {
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
    default RequestStream sendAndRequest(String event, Entity content, long timeout) throws IOException {
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
    RequestStream sendAndRequest(String event, Entity content, long timeout, Consumer<RequestStream> consumer) throws IOException;

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event   事件
     * @param content 内容
     * @return 流
     */
    default SubscribeStream sendAndSubscribe(String event, Entity content) throws IOException {
        return sendAndSubscribe(event, content, 0, null);
    }

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event   事件
     * @param content 内容
     * @return 流
     */
    default SubscribeStream sendAndSubscribe(String event, Entity content, Consumer<SubscribeStream> consumer) throws IOException {
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
    default SubscribeStream sendAndSubscribe(String event, Entity content, long timeout) throws IOException {
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
    SubscribeStream sendAndSubscribe(String event, Entity content, long timeout, Consumer<SubscribeStream> consumer) throws IOException;
}
