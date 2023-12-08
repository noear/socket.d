package org.noear.socketd.transport.core;

/**
 * 流接收器
 *
 * @author noear
 * @since 1.0
 */
public interface StreamAcceptor {
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
     * 接收答复流
     *
     * @param message 消息
     * @param channel 通道
     */
    void accept(Message message, Channel channel);
}