package org.noear.socketd.core;

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

    public ListenerPipeline prev(Listener listener) {
        deque.addFirst(listener);
        return this;
    }

    public ListenerPipeline next(Listener listener) {
        deque.addLast(listener);
        return this;
    }

    @Override
    public void onOpen(Session session) throws IOException{
        for (Listener listener : deque) {
            listener.onOpen(session);
        }
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        for (Listener listener : deque) {
            listener.onMessage(session, message);
        }
    }

    @Override
    public void onClose(Session session) {
        for (Listener listener : deque) {
            listener.onClose(session);
        }
    }

    @Override
    public void onError(Session session, Throwable error) {
        for (Listener listener : deque) {
            listener.onError(session, error);
        }
    }
}
