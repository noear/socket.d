package org.noear.socketd.core.impl;

import org.noear.socketd.core.Acceptor;
import org.noear.socketd.core.Entity;
import org.noear.socketd.core.Message;

import java.util.concurrent.CompletableFuture;

/**
 * 请求接收器
 *
 * @author noear
 * @since 2.0
 */
public class AcceptorRequest implements Acceptor {
    private final CompletableFuture<Entity> future;

    public AcceptorRequest(CompletableFuture<Entity> future) {
        this.future = future;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public boolean accept(Message message) {
        return future.complete(message.getEntity());
    }
}
