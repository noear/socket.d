package org.noear.socketd.transport.core.listener;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 管道监听器
 *
 * @author noear
 * @since 2.0
 */
public class PipelineListener implements Listener {
    protected final Deque<Listener> deque = new LinkedList<>();

    /**
     * 前一个
     */
    public PipelineListener prev(Listener listener) {
        deque.addFirst(listener);
        return this;
    }

    /**
     * 后一个
     */
    public PipelineListener next(Listener listener) {
        deque.addLast(listener);
        return this;
    }

    /**
     * 数量（二级监听器的数据）
     * */
    public int size(){
        return deque.size();
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