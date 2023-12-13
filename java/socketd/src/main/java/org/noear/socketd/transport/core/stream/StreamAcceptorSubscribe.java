package org.noear.socketd.transport.core.stream;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.utils.IoConsumer;

/**
 * 订阅流接收器
 *
 * @author noear
 * @since 2.0
 */
public class StreamAcceptorSubscribe extends StreamAcceptorBase {
    private final IoConsumer<Reply> future;

    public StreamAcceptorSubscribe(String sid, long timeout, IoConsumer<Reply> future) {
        super(sid, timeout);
        this.future = future;
    }

    /**
     * 是否单发接收
     */
    @Override
    public boolean isSingle() {
        return false;
    }

    /**
     * 是否结束接收
     */
    @Override
    public boolean isDone() {
        return false;
    }

    /**
     * 接收时
     */
    @Override
    public void onAccept(MessageInternal message, Channel channel) {
        try {
            future.accept(message);
        } catch (Throwable e) {
            channel.onError(e);
        }
    }
}
