package org.noear.socketd.transport.core.stream;

import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.StreamAcceptorBase;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.utils.IoConsumer;

/**
 * 订阅流接收器
 *
 * @author noear
 * @since 2.0
 */
public class StreamAcceptorSubscribe extends StreamAcceptorBase {
    private final IoConsumer<Entity> future;

    public StreamAcceptorSubscribe(IoConsumer<Entity> future) {
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
     * 超时设定（单位：毫秒）
     */
    @Override
    public long timeout() {
        return 0;
    }

    /**
     * 接收答复流
     */
    @Override
    public void accept(Message message, Channel channel) {
        try {
            future.accept(message);
        } catch (Throwable e) {
            channel.onError(e);
        }
    }
}