package org.noear.socketd.transport.core;

import java.util.function.Consumer;

/**
 * 流接口
 *
 * @author noear
 * @since 2.1
 */
public interface Stream {
    /**
     * 流Id
     */
    String sid();

    /**
     * 是否单收
     */
    boolean isSingle();

    /**
     * 是否完成
     */
    boolean isDone();

    /**
     * 超时设定（单位：毫秒）
     */
    long timeout();

    /**
     * 异常发生时
     */
    Stream thenError(Consumer<Throwable> onError);
}
