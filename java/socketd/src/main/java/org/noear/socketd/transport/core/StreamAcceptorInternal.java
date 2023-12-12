package org.noear.socketd.transport.core;

/**
 * 流接收器内部扩展
 *
 * @author noear
 * @since 2.0
 */
public interface StreamAcceptorInternal extends StreamAcceptor{

    /**
     * 接收时
     *
     * @param message 消息
     * @param channel 通道
     */
    void onAccept(Message message, Channel channel);

    /**
     * 异常时
     *
     * @param error 异常
     */
    void onError(Throwable error);
}