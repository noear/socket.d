package org.noear.socketd.transport.core.stream;

import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.StreamAcceptorBase;

import java.util.concurrent.CompletableFuture;

/**
 * 请求流接收器
 *
 * @author noear
 * @since 2.0
 */
public class StreamAcceptorRequest extends StreamAcceptorBase {
    private final CompletableFuture<Entity> future;

    public StreamAcceptorRequest(String sid, long timeout, CompletableFuture<Entity> future) {
        super(sid, timeout);
        this.future = future;
    }

    /**
     * 是否单发接收
     */
    @Override
    public boolean isSingle() {
        return true;
    }

    /**
     * 是否结束接收
     */
    @Override
    public boolean isDone() {
        return future.isDone();
    }

    /**
     * 接收时
     */
    @Override
    public void onAccept(Message message, Channel channel) {
        future.complete(message);
    }
}
