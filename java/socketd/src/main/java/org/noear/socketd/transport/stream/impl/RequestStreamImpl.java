package org.noear.socketd.transport.stream.impl;

import org.noear.socketd.exception.SocketDException;
import org.noear.socketd.exception.SocketDTimeoutException;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.stream.RequestStream;
import org.noear.socketd.utils.IoConsumer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * 请求流
 *
 * @author noear
 * @since 2.0
 */
public class RequestStreamImpl extends StreamBase<RequestStream> implements RequestStream {
    private final CompletableFuture<Reply> future;

    public RequestStreamImpl(String sid, long timeout) {
        super(sid, Constants.DEMANDS_SINGLE, timeout);
        this.future = new CompletableFuture<>();
    }

    /**
     * 是否完成的
     */
    @Override
    public boolean isDone() {
        return future.isDone();
    }

    /**
     * 出错时
     */
    @Override
    public void onError(Throwable error) {
        super.onError(error);

        future.completeExceptionally(error);
    }

    /**
     * 答复时
     */
    @Override
    public void onReply(MessageInternal reply) {
        future.complete(reply);
    }

    @Override
    public Reply await() {
        try {
            return future.get(timeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new SocketDTimeoutException("Request reply timeout > " + timeout() + ", sid=" + sid());
        } catch (Throwable e) {
            if (e instanceof ExecutionException) {
                e = e.getCause();
            }

            if (e instanceof SocketDException) {
                throw (SocketDException) e;
            } else {
                throw new SocketDException("Request failed, sid=" + sid(), e);
            }
        }
    }

    @Override
    public RequestStream thenReply(IoConsumer<Reply> onReply) {
        future.thenAccept((r) -> {
            try {
                onReply.accept(r);
            } catch (Throwable e) {
                onError(e);
            }
        });

        return this;
    }
}