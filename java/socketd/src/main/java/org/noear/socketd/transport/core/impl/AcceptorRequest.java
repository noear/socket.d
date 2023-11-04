package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.Acceptor;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Message;

import java.util.concurrent.CompletableFuture;

/**
 * 请求接收器
 *
 * @author noear
 * @since 2.0
 */
public class AcceptorRequest implements Acceptor {
    private final CompletableFuture<Entity> future;
    private final long timeout;

    public AcceptorRequest(CompletableFuture<Entity> future, long timeout) {
        this.future = future;
        this.timeout = timeout;
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
    public long timeout() {
        return timeout;
    }

    @Override
    public void accept(Message message) {
        future.complete(message.getEntity());
    }
}
