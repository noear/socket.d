package org.noear.socketd.transport.core;

import java.util.function.Consumer;

/**
 * 流接收器
 *
 * @author noear
 * @since 2.1
 */
public interface StreamAcceptor {
    /**
     * 流Id
     */
    String sid();

    /**
     * 是否单发接收
     */
    boolean isSingle();

    /**
     * 是否结束接收
     */
    boolean isDone();

    /**
     * 超时设定（单位：毫秒）
     */
    long timeout();

    /**
     * 异常发生时
     */
    StreamAcceptor thenError(Consumer<Throwable> onError);
}
