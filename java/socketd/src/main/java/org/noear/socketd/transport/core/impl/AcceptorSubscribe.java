package org.noear.socketd.transport.core.impl;


import org.noear.socketd.transport.core.Acceptor;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Message;

import java.util.function.Consumer;

/**
 * 订阅接收器
 *
 * @author noear
 * @since 2.0
 */
public class AcceptorSubscribe implements Acceptor {
    private final Consumer<Entity> future;
    public AcceptorSubscribe(Consumer<Entity> future){
        this.future = future;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public long timeout() {
        return 0;
    }

    @Override
    public void accept(Message message) {
        future.accept(message.getEntity());
    }
}
