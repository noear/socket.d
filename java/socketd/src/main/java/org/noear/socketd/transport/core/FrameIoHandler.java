package org.noear.socketd.transport.core;

import org.noear.socketd.utils.IoCompletionHandler;

/**
 * 帧输入输出处理器
 *
 * @author noear
 * @since 2.5
 */
public interface FrameIoHandler {
    /**
     * 发送帧
     *
     * @param channel          通道
     * @param frame            帧
     * @param channelAssistant 通道助理
     * @param target           发送目标
     */
    <S> void sendFrameHandle(ChannelInternal channel, Frame frame, ChannelAssistant<S> channelAssistant, S target, IoCompletionHandler completionHandler);


    /**
     * 接收帧
     *
     * @param channel 通道
     * @param frame   帧
     */
    void reveFrameHandle(ChannelInternal channel, Frame frame);
}
