package org.noear.socketd.transport.core;

import org.noear.socketd.transport.core.listener.SimpleListener;

import java.io.IOException;

/**
 * 监听器包装
 *
 * @author noear
 * @since 2.4
 */
public class ListenerWrapper implements Listener {
    private Listener listener = new SimpleListener();

    public void setListener(Listener listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }

    @Override
    public void onOpen(Session session) throws IOException {
        listener.onOpen(session);
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        listener.onMessage(session, message);
    }

    @Override
    public void onClose(Session session) {
        listener.onClose(session);
    }

    @Override
    public void onError(Session session, Throwable error) {
        listener.onError(session, error);
    }
}
