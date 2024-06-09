package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.stream.RequestStream;
import org.noear.socketd.transport.stream.SendStream;
import org.noear.socketd.transport.stream.SubscribeStream;

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
     * 是否活跃
     */
    boolean isActive();

    /**
     * 是否正在关闭中
     */
    boolean isClosing();

    /**
     * 获取会话Id
     */
    String sessionId();

    /**
     * 发送
     *
     * @param event  事件
     * @param entity 实体
     * @return 流
     */
    SendStream send(String event, Entity entity) throws IOException;

    /**
     * 发送并请求
     *
     * @param event  事件
     * @param entity 实体
     * @return 流
     */
    default RequestStream sendAndRequest(String event, Entity entity) throws IOException {
        return sendAndRequest(event, entity, 0L);
    }

    /**
     * 发送并请求
     *
     * @param event   事件
     * @param entity  实体
     * @param timeout 超时（单位：毫秒）
     * @return 流
     */
    RequestStream sendAndRequest(String event, Entity entity, long timeout) throws IOException;


    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event  事件
     * @param entity 实体
     * @return 流
     */
    default SubscribeStream sendAndSubscribe(String event, Entity entity) throws IOException {
        return sendAndSubscribe(event, entity, 0L);
    }

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event   事件
     * @param entity  实体
     * @param timeout 超时（单位：毫秒）
     * @return 流
     */
    SubscribeStream sendAndSubscribe(String event, Entity entity, long timeout) throws IOException;

    /**
     * 关闭开始
     *
     * @deprecated 2.4
     */
    @Deprecated
    default void closeStarting() throws IOException {
        preclose();
    }

    /**
     * 预关闭（发送预关闭指令，通知对端不要再主动发消息过来了）
     */
    void preclose() throws IOException;

    /**
     * 关闭（发送关闭指令，并关闭连接）
     */
    void close() throws IOException;

    /**
     * 关闭代码
     */
    int closeCode();

    /**
     * 手动重连（一般是自动）
     */
    void reconnect() throws IOException;
}
