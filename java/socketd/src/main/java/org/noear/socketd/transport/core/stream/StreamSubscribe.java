package org.noear.socketd.transport.core.stream;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.utils.IoConsumer;

/**
 * 订阅流
 *
 * @author noear
 * @since 2.0
 */
public class StreamSubscribe extends StreamBase {
    private final IoConsumer<Reply> future;

    public StreamSubscribe(String sid, long timeout, IoConsumer<Reply> future) {
        super(sid, false, timeout);
        this.future = future;
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
    public void onAccept(MessageInternal reply, Channel channel) {
        try {
            future.accept(reply);
        } catch (Throwable e) {
            channel.onError(e);
        }
    }
}