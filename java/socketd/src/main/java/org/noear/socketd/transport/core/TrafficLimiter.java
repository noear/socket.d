package org.noear.socketd.transport.core;

import org.noear.socketd.utils.IoCompletionHandler;

/**
 * 流量限制器
 *
 * @author noear
 * @since 2.5
 */
public interface TrafficLimiter {
    /**
     * 发送帧（在写锁范围，才有效）
     *
     * @param frameIoHandler   帧输入输出处理
     * @param channel          通道
     * @param frame            帧
     * @param channelAssistant 通道助理
     * @param target           发送目标
     */
    <S> void sendFrame(FrameIoHandler frameIoHandler, ChannelInternal channel, Frame frame, ChannelAssistant<S> channelAssistant, S target, IoCompletionHandler completionHandler);

    /**
     * 接收帧（在读线程里，才有效）
     *
     * @param frameIoHandler 帧输入输出处理
     * @param channel        通道
     * @param frame          帧
     */
    void reveFrame(FrameIoHandler frameIoHandler, ChannelInternal channel, Frame frame);
}
