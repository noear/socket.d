package org.noear.socketd.protocol.impl;

import org.noear.socketd.protocol.Acceptor;
import org.noear.socketd.protocol.Entity;
import org.noear.socketd.protocol.Payload;

import java.util.concurrent.CompletableFuture;

/**
 * 订阅接收器
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
    public boolean accept(Payload payload) {
        return future.complete(payload.getEntity());
    }
}
