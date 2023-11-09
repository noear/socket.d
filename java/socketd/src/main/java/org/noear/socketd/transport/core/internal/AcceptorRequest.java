package org.noear.socketd.transport.core.internal;

import org.noear.socketd.transport.core.Acceptor;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Message;

import java.util.concurrent.CompletableFuture;

/**
 * 请求答复接收器
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

    /**
     * 是否单发接收
     * */
    @Override
    public boolean isSingle() {
        return true;
    }

    /**
     * 是否结束接收
     * */
    @Override
    public boolean isDone() {
        return future.isDone();
    }

    /**
     * 超时设定（单位：毫秒）
     * */
    @Override
    public long timeout() {
        return timeout;
    }

    /**
     * 接收答复
     * */
    @Override
    public void accept(Message message) {
        future.complete(message);
    }
}
