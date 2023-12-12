package org.noear.socketd.transport.core;

import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

/**
 * 流接收器基类
 *
 * @author noear
 * @since 2.0
 */
public abstract class StreamAcceptorBase implements StreamAcceptorInternal {
    public ScheduledFuture<?> insuranceFuture;

    private final String sid;
    private final long timeout;
    private Consumer<Throwable> onError;

    public StreamAcceptorBase(String sid, long timeout) {
        this.sid = sid;
        this.timeout = timeout;
    }

    @Override
    public String sid() {
        return sid;
    }

    @Override
    public long timeout() {
        return timeout;
    }

    @Override
    public void onError(Throwable error) {
        if (onError != null) {
            onError.accept(error);
        }
    }

    @Override
    public StreamAcceptor thenError(Consumer<Throwable> onError) {
        this.onError = onError;
        return this;
    }
}
