package org.noear.socketd.transport.core.listener;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.router.Router;
import org.noear.socketd.transport.core.router.RouterHashMap;

import java.io.IOException;

/**
 * 路由监听器（根据握手地址路由，一般用于服务端）
 *
 * @author noear
 * @since 2.0
 */
public class RouterListener implements Listener {
    protected final Router router;

    public RouterListener() {
        this.router = new RouterHashMap();
    }

    public RouterListener(Router router) {
        this.router = router;
    }

    /**
     * 路由
     */
    public RouterListener of(String path, Listener listener) {
        router.add(path, listener);
        return this;
    }

    /**
     * 路由
     */
    public BuilderListener of(String path) {
        BuilderListener l1 = new BuilderListener();
        router.add(path, l1);
        return l1;
    }

    /**
     * 数量
     */
    public int count() {
        return router.count();
    }

    @Override
    public void onOpen(Session session) throws IOException {
        Listener l1 = router.matching(session.getPath());

        if (l1 != null) {
            l1.onOpen(session);
        }
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        Listener l1 = router.matching(session.getPath());

        if (l1 != null) {
            l1.onMessage(session, message);
        }
    }

    @Override
    public void onClose(Session session) {
        Listener l1 = router.matching(session.getPath());

        if (l1 != null) {
            l1.onClose(session);
        }
    }

    @Override
    public void onError(Session session, Throwable error) {
        Listener l1 = router.matching(session.getPath());

        if (l1 != null) {
            l1.onError(session, error);
        }
    }
}