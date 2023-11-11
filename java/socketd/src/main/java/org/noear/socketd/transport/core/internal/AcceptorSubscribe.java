package org.noear.socketd.transport.core.internal;


import org.noear.socketd.transport.core.Acceptor;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.utils.IoConsumer;

import java.io.IOException;

/**
 * 订阅答复接收器
 *
 * @author noear
 * @since 2.0
 */
public class AcceptorSubscribe implements Acceptor {
    private final IoConsumer<Entity> future;
    public AcceptorSubscribe(IoConsumer<Entity> future){
        this.future = future;
    }

    /**
     * 是否单发接收
     * */
    @Override
    public boolean isSingle() {
        return false;
    }

    /**
     * 是否结束接收
     * */
    @Override
    public boolean isDone() {
        return false;
    }

    /**
     * 超时设定（单位：毫秒）
     * */
    @Override
    public long timeout() {
        return 0;
    }

    /**
     * 接收答复
     * */
    @Override
    public void accept(Message message) throws IOException {
        future.accept(message);
    }
}
