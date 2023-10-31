package org.noear.socketd.protocol.impl;


import org.noear.socketd.protocol.Acceptor;
import org.noear.socketd.protocol.Entity;
import org.noear.socketd.protocol.Message;

import java.util.function.Consumer;

/**
 * 请求接收器
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
    public boolean accept(Message message) {
        future.accept(message.getEntity());
        return true;
    }
}
