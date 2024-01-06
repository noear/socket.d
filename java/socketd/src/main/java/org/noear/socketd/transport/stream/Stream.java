package org.noear.socketd.transport.stream;

import org.noear.socketd.utils.TriConsumer;

import java.util.function.Consumer;

/**
 * 流接口
 *
 * @author noear
 * @since 2.1
 */
public interface Stream<T extends Stream> {
    /**
     * 流Id
     */
    String sid();

    /**
     * 是否完成
     */
    boolean isDone();

    /**
     * 异常发生时
     */
    T thenError(Consumer<Throwable> onError);

    /**
     * 进度发生时
     *
     * @param onProgress (isSend, val, max)
     */
    T thenProgress(TriConsumer<Boolean, Integer, Integer> onProgress);
}
