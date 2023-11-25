package org.noear.socketd.transport.core.listener;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;

import java.io.IOException;

/**
 * 路径监听器（根据握手地址映射，一般用于服务端）
 *
 * @author noear
 * @since 2.0
 */
public class PathListener implements Listener {
    protected final PathMapper mapper;

    public PathListener() {
        this.mapper = new PathMapperDefault();
    }

    public PathListener(PathMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 映射
     */
    public PathListener of(String path, Listener listener) {
        mapper.put(path, listener);
        return this;
    }

    /**
     * 映射
     */
    public RouteListener of(String path) {
        RouteListener l1 = new RouteListener();
        mapper.put(path, l1);
        return l1;
    }

    /**
     * 数量（二级监听器的数据）
     */
    public int size() {
        return mapper.size();
    }

    @Override
    public void onOpen(Session session) throws IOException {
        Listener l1 = mapper.get(session.path());

        if (l1 != null) {
            l1.onOpen(session);
        }
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        Listener l1 = mapper.get(session.path());

        if (l1 != null) {
            l1.onMessage(session, message);
        }
    }

    @Override
    public void onClose(Session session) {
        Listener l1 = mapper.get(session.path());

        if (l1 != null) {
            l1.onClose(session);
        }
    }

    @Override
    public void onError(Session session, Throwable error) {
        Listener l1 = mapper.get(session.path());

        if (l1 != null) {
            l1.onError(session, error);
        }
    }
}