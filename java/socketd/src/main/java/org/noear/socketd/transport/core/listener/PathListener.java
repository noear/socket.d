package org.noear.socketd.transport.core.listener;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;

import java.io.IOException;

/**
 * 路径监听器（根据握手地址路由，一般用于服务端）
 *
 * @author noear
 * @since 2.0
 */
public class PathListener implements Listener {
    /**
     * 路径路由选择器
     * */
    protected final RouteSelector<Listener> pathRouteSelector;

    public PathListener() {
        this.pathRouteSelector = new RouteSelectorDefault<>();
    }

    public PathListener(RouteSelector<Listener> routeSelector) {
        this.pathRouteSelector = routeSelector;
    }

    /**
     * 路由
     */
    public PathListener of(String path, Listener listener) {
        pathRouteSelector.put(path, listener);
        return this;
    }

    /**
     * 数量（二级监听器的数据）
     */
    public int size() {
        return pathRouteSelector.size();
    }

    @Override
    public void onOpen(Session session) throws IOException {
        Listener l1 = pathRouteSelector.select(session.path());

        if (l1 != null) {
            l1.onOpen(session);
        }
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        Listener l1 = pathRouteSelector.select(session.path());

        if (l1 != null) {
            l1.onMessage(session, message);
        }
    }

    @Override
    public void onClose(Session session) {
        Listener l1 = pathRouteSelector.select(session.path());

        if (l1 != null) {
            l1.onClose(session);
        }
    }

    @Override
    public void onError(Session session, Throwable error) {
        Listener l1 = pathRouteSelector.select(session.path());

        if (l1 != null) {
            l1.onError(session, error);
        }
    }
}