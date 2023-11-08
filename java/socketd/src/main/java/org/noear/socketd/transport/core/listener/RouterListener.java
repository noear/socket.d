package org.noear.socketd.transport.core.listener;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 路由监听器（根据握手地址路由，一般用于服务端）
 *
 * @author noear
 * @since 2.0
 */
public class RouterListener implements Listener {
    protected final Map<String, Listener> routingTable = new HashMap<>();

    /**
     * 路由
     */
    public RouterListener of(String path, Listener listener) {
        routingTable.put(path, listener);
        return this;
    }

    /**
     * 路由
     */
    public BuilderListener of(String path) {
        BuilderListener listener = new BuilderListener();
        routingTable.put(path, listener);
        return listener;
    }

    /**
     * 匹配
     */
    protected Listener matching(Session session) {
        return routingTable.get(session.getHandshake().getPath());
    }

    @Override
    public void onOpen(Session session) throws IOException {
        Listener listener = matching(session);

        if (listener != null) {
            listener.onOpen(session);
        }
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        Listener listener = matching(session);

        if (listener != null) {
            listener.onMessage(session, message);
        }
    }

    @Override
    public void onClose(Session session) {
        Listener listener = matching(session);

        if (listener != null) {
            listener.onClose(session);
        }
    }

    @Override
    public void onError(Session session, Throwable error) {
        Listener listener = matching(session);

        if (listener != null) {
            listener.onError(session, error);
        }
    }
}
