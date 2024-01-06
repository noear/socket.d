package org.noear.socketd.utils;

/**
 * 三消费者
 *
 * @author noear
 * @since 2.3
 */
@FunctionalInterface
public interface TriConsumer<T,U,X> {
    void accept(T t, U u, X x);
}
