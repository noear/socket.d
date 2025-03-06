package org.noear.socketd.transport.core.listener;

import org.noear.socketd.transport.core.*;

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
     */
    protected final RouteSelector<Listener> pathRouteSelector;

    public PathListener() {
        this.pathRouteSelector = new RouteSelectorDefault<>();
    }

    public PathListener(RouteSelector<Listener> routeSelector) {
        this.pathRouteSelector = routeSelector;
    }

    /**
     * 路由
     *
     * @param path     路径
     * @param listener 监听器
     * @return this
     * @since 2.3
     */
    public PathListener doOf(String path, Listener listener) {
        pathRouteSelector.put(path, listener);
        return this;
    }

    /**
     * 路由
     *
     * @param path 路径
     * @return 事件监听器
     * @since 2.3
     */
    public EventListener of(String path) {
        EventListener listener = new EventListener();
        pathRouteSelector.put(path, listener);
        return listener;
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

    /**
     * 收到答复时
     *
     * @param session 会话
     * @param message 消息
     */
    @Override
    public void onReply(Session session, Message message) {
        Listener l1 = pathRouteSelector.select(session.path());

        if (l1 != null) {
            l1.onReply(session, message);
        }
    }

    /**
     * 发送消息时
     *
     * @param session 会话
     * @param message 消息
     */
    @Override
    public void onSend(Session session, Message message) {
        Listener l1 = pathRouteSelector.select(session.path());

        if (l1 != null) {
            l1.onSend(session, message);
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