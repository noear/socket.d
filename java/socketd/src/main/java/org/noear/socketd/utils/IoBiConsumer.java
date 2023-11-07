package org.noear.socketd.utils;

import java.io.IOException;

/**
 * @author noear
 * @since 2.0
 */
@FunctionalInterface
public interface IoBiConsumer<T,U> {
    void accept(T t, U u) throws IOException;
}
