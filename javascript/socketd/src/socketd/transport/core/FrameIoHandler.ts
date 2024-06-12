import type {ChannelInternal} from "./Channel";
import {Frame} from "./Frame";
import {ChannelAssistant} from "./ChannelAssistant";
import {IoBiConsumer} from "./Typealias";

/**
 * 帧输入输出处理器（为 TrafficLimiter 提供支持）
 * */
export interface FrameIoHandler {
    /**
     * 发送帧
     *
     * @param channel          通道
     * @param frame            帧
     * @param channelAssistant 通道助理
     * @param target           发送目标
     */
    sendFrameHandle<S>(channel: ChannelInternal, frame: Frame, channelAssistant: ChannelAssistant<S>, target: S, completionHandler:IoBiConsumer<Boolean, Error|any>);

    /**
     * 接收帧
     *
     * @param channel 通道
     * @param frame   帧
     */
    reveFrameHandle(channel: ChannelInternal, frame: Frame);
}