package org.noear.socketd.transport.core.listener;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.utils.IoBiConsumer;
import org.noear.socketd.utils.IoConsumer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 路由监听器（根据消息路由映射）
 *
 * @author noear
 * @since 2.0
 */
public class RouteListener implements Listener {
    private IoConsumer<Session> onOpenHandler;
    private IoBiConsumer<Session, Message> onMessageHandler;
    private Consumer<Session> onCloseHandler;
    private BiConsumer<Session, Throwable> onErrorHandler;
    private Map<String, IoBiConsumer<Session, Message>> onMessageRouting = new ConcurrentHashMap<>();

    //for builder
    public RouteListener onOpen(IoConsumer<Session> onOpen) {
        this.onOpenHandler = onOpen;
        return this;
    }

    public RouteListener onMessage(IoBiConsumer<Session, Message> onMessage) {
        this.onMessageHandler = onMessage;
        return this;
    }

    public RouteListener onClose(Consumer<Session> onClose) {
        this.onCloseHandler = onClose;
        return this;
    }

    public RouteListener onError(BiConsumer<Session, Throwable> onError) {
        this.onErrorHandler = onError;
        return this;
    }

    public RouteListener on(String route, IoBiConsumer<Session, Message> handler) {
        onMessageRouting.put(route, handler);
        return this;
    }


    // for Listener

    @Override
    public void onOpen(Session session) throws IOException {
        if (onOpenHandler != null) {
            onOpenHandler.accept(session);
        }
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        if (onMessageHandler != null) {
            onMessageHandler.accept(session, message);
        }

        IoBiConsumer<Session, Message> messageHandler = onMessageRouting.get(message.route());
        if (messageHandler != null) {
            messageHandler.accept(session, message);
        }
    }

    @Override
    public void onClose(Session session) {
        if (onCloseHandler != null) {
            onCloseHandler.accept(session);
        }
    }

    @Override
    public void onError(Session session, Throwable error) {
        if (onErrorHandler != null) {
            onErrorHandler.accept(session, error);
        }
    }
}
