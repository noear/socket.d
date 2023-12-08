package org.noear.socketd.transport.core.stream;

import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.StreamAcceptorBase;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Message;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 请求流接收器
 *
 * @author noear
 * @since 2.0
 */
public class StreamAcceptorRequest extends StreamAcceptorBase {
    private final CompletableFuture<Entity> future;
    private final long timeout;

    public StreamAcceptorRequest(CompletableFuture<Entity> future, long timeout) {

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
     * 接收答复流
     * */
    @Override
    public void accept(Message message, Channel channel) {
        future.complete(message);
    }
}
