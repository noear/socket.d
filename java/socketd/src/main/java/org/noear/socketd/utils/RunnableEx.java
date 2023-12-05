package org.noear.socketd.utils;

/**
 * 可运行
 *
 * @author noear
 * @since 2.0
 * */
public interface RunnableEx<Throw extends Throwable> {
    void run() throws Throw;
}
