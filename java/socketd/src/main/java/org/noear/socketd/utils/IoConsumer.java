package org.noear.socketd.utils;

import java.io.IOException;

/**
 * @author noear
 * @since 2.0
 */
@FunctionalInterface
public interface IoConsumer<T> {
    void accept(T t) throws IOException;
}
