package org.noear.socketd.transport.core.listener;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.utils.IoBiConsumer;
import org.noear.socketd.utils.IoConsumer;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 事件监听器（根据消息事件路由）
 *
 * @author noear
 * @since 2.0
 */
public class EventListener implements Listener {
    private IoConsumer<Session> doOnOpenHandler;
    private MessageHandler doOnMessageHandler;
    private Consumer<Session> doOnCloseHandler;
    private BiConsumer<Session, Throwable> doOnErrorHandler;

    /**
     * 事件路由选择器
     * */
    private final RouteSelector<MessageHandler> eventRouteSelector;

    public EventListener(){
        eventRouteSelector = new RouteSelectorDefault<>();
    }

    public EventListener(RouteSelector<MessageHandler> routeSelector){
        eventRouteSelector = routeSelector;
    }

    //for builder
    public EventListener doOnOpen(IoConsumer<Session> onOpen) {
        this.doOnOpenHandler = onOpen;
        return this;
    }

    public EventListener doOnMessage(MessageHandler onMessage) {
        this.doOnMessageHandler = onMessage;
        return this;
    }

    public EventListener doOnClose(Consumer<Session> onClose) {
        this.doOnCloseHandler = onClose;
        return this;
    }

    public EventListener doOnError(BiConsumer<Session, Throwable> onError) {
        this.doOnErrorHandler = onError;
        return this;
    }

    public EventListener doOn(String event, MessageHandler handler) {
        eventRouteSelector.put(event, handler);
        return this;
    }


    // for Listener

    @Override
    public void onOpen(Session session) throws IOException {
        if (doOnOpenHandler != null) {
            doOnOpenHandler.accept(session);
        }
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        if (doOnMessageHandler != null) {
            doOnMessageHandler.handle(session, message);
        }

        MessageHandler messageHandler = eventRouteSelector.select(message.event());
        if (messageHandler != null) {
            messageHandler.handle(session, message);
        }
    }

    @Override
    public void onClose(Session session) {
        if (doOnCloseHandler != null) {
            doOnCloseHandler.accept(session);
        }
    }

    @Override
    public void onError(Session session, Throwable error) {
        if (doOnErrorHandler != null) {
            doOnErrorHandler.accept(session, error);
        }
    }
}
