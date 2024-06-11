package org.noear.socketd.transport.core;

import java.io.IOException;

/**
 * 监听器
 *
 * @author noear
 * @since 2.0
 */
public interface Listener {
    /**
     * 打开时
     *
     * @param session 会话
     */
    void onOpen(Session session) throws IOException;

    /**
     * 收到消息时
     *
     * @param session 会话
     * @param message 消息
     */
    void onMessage(Session session, Message message) throws IOException;

    /**
     * 收到答复时
     *
     * @param session 会话
     * @param message 消息
     */
    default void onReply(Session session, Message message) {
    }

    /**
     * 发送消息时
     *
     * @param session 会话
     * @param message 消息
     */
    default void onSend(Session session, Message message) {
    }

    /**
     * 关闭时
     *
     * @param session 会话
     */
    void onClose(Session session);

    /**
     * 出错时
     *
     * @param session 会话
     * @param error   错误信息
     */
    void onError(Session session, Throwable error);
}