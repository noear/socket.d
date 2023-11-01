package org.noear.socketd.core;


/**
 * 答复接收器
 *
 * @author noear
 * @since 1.0
 */
public interface Acceptor {
    /**
     * 是否单发接收
     * */
    boolean isSingle();

    /**
     * 是否结束接收
     * */
    boolean isDone();

    /**
     * 接收
     * */
    boolean accept(Message message);
}