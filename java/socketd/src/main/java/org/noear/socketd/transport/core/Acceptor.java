package org.noear.socketd.transport.core;


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
     * 超时设定（单位：毫秒）
     * */
    long timeout();

    /**
     * 接收答复
     * */
    void accept(Message message);
}