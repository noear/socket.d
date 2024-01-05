package org.noear.socketd.transport.core.stream;

import org.noear.socketd.exception.SocketdChannelException;
import org.noear.socketd.exception.SocketdException;
import org.noear.socketd.exception.SocketdTimeoutException;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.utils.IoConsumer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * 请求流
 *
 * @author noear
 * @since 2.0
 */
public class StreamRequestImpl extends StreamBase<StreamRequest> implements StreamRequest {
    private final CompletableFuture<Reply> future;

    public StreamRequestImpl(Channel channel, String sid, long timeout) {
        super(channel, sid, 1, timeout);
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
     * 答复时
     */
    @Override
    public void onReply(MessageInternal reply, Channel channel) {
        setChannel(channel);

        future.complete(reply);
    }

    @Override
    public Reply await() {
        try {
            return future.get(timeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            if (channel().isValid()) {
                throw new SocketdTimeoutException("Request reply timeout > " + timeout() + ", sid=" + sid());
            } else {
                throw new SocketdChannelException("This channel is closed, sid=" + sid());
            }
        } catch (Throwable e) {
            throw new SocketdException("Send and request failed, sid=" + sid(), e);
        }
    }

    @Override
    public StreamRequest thenReply(IoConsumer<Reply> onReply) {
        future.thenAccept((r) -> {
            try {
                onReply.accept(r);
            } catch (Throwable eh) {
                channel().onError(eh);
            }
        });

        return this;
    }
}
