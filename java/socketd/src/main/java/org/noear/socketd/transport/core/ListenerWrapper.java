package org.noear.socketd.transport.core;

import java.io.IOException;

/**
 * 监听器包装
 *
 * @author noear
 * @since 2.4
 */
public class ListenerWrapper implements Listener {
    private Listener listener;

    public ListenerWrapper wrap(Listener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onOpen(Session session) throws IOException {
        if (listener != null) {
            listener.onOpen(session);
        }
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        if (listener != null) {
            listener.onMessage(session, message);
        }
    }

    @Override
    public void onClose(Session session) {
        if (listener != null) {
            listener.onClose(session);
        }
    }

    @Override
    public void onError(Session session, Throwable error) {
        if (listener != null) {
            listener.onError(session, error);
        }
    }
}
