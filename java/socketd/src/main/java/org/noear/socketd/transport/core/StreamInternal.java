package org.noear.socketd.transport.core;

/**
 * 流内部接口
 *
 * @author noear
 * @since 2.0
 */
public interface StreamInternal extends Stream {

    /**
     * 接收时
     *
     * @param reply   答复
     * @param channel 通道
     */
    void onAccept(MessageInternal reply, Channel channel);

    /**
     * 异常时
     *
     * @param error 异常
     */
    void onError(Throwable error);
}