package org.noear.socketd.transport.core;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 监听管道
 *
 * @author noear
 * @since 2.0
 */
public class ListenerPipeline implements Listener {
    Deque<Listener> deque = new LinkedList<>();

    /**
     * 前一个
     */
    public ListenerPipeline prev(Listener listener) {
        deque.addFirst(listener);
        return this;
    }

    /**
     * 后一个
     */
    public ListenerPipeline next(Listener listener) {
        deque.addLast(listener);
        return this;
    }

    /**
     * 打开时
     *
     * @param session 会话
     */
    @Override
    public void onOpen(Session session) throws IOException {
        for (Listener listener : deque) {
            listener.onOpen(session);
        }
    }

    /**
     * 收到消息时
     *
     * @param session 会话
     * @param message 消息
     */
    @Override
    public void onMessage(Session session, Message message) throws IOException {
        for (Listener listener : deque) {
            listener.onMessage(session, message);
        }
    }

    /**
     * 关闭时
     *
     * @param session 会话
     */
    @Override
    public void onClose(Session session) {
        for (Listener listener : deque) {
            listener.onClose(session);
        }
    }

    /**
     * 出错时
     *
     * @param session 会话
     * @param error   错误信息
     */
    @Override
    public void onError(Session session, Throwable error) {
        for (Listener listener : deque) {
            listener.onError(session, error);
        }
    }
}