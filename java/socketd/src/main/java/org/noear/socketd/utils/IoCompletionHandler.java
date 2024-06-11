package org.noear.socketd.utils;

/**
 * @author noear
 * @since 2.5
 */
public interface IoCompletionHandler {
    void completed(boolean result, Throwable throwable);
}
