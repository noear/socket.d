package org.noear.socketd.transport.core;

import java.io.IOException;

/**
 * 处理器
 *
 * @author noear
 * @since 2.0
 */
public interface Processor {
    /**
     * 设置监听
     */
    void setListener(Listener listener);

    /**
     * 接收处理
     */
    void onReceive(Channel channel, Frame frame);

    /**
     * 打开时
     *
     * @param channel 通道
     */
    void onOpen(Channel channel);

    /**
     * 收到消息时
     *
     * @param channel 通道
     * @param message 消息
     */
    void onMessage(Channel channel, Message message);

    /**
     * 关闭时
     *
     * @param channel 通道
     */
    void onClose(Channel channel);

    /**
     * 出错时
     *
     * @param channel 通道
     * @param error   错误信息
     */
    void onError(Channel channel, Throwable error);
}