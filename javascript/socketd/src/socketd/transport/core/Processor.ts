import {Listener} from "./Listener";
import type {ChannelInternal} from "./Channel";
import {Frame} from "./Frame";
import {StreamInternal} from "../stream/Stream";
import {ChannelAssistant} from "./ChannelAssistant";

/**
 * 处理器
 *
 * @author noear
 * @since 2.0
 */
export interface Processor {
    /**
     * 设置监听器
     */
    setListener(listener: Listener);

    /**
     * 发送帧
     * */
    sendFrame<S>(channel: ChannelInternal, frame: Frame, channelAssistant:ChannelAssistant<S>, target:S );

    /**
     * 接收帧
     */
    reveFrame(channel: ChannelInternal, frame: Frame);

    /**
     * 打开时
     *
     * @param channel 通道
     */
    onOpen(channel: ChannelInternal);

    /**
     * 收到消息时
     *
     * @param channel 通道
     * @param frame   帧
     */
    onMessage(channel: ChannelInternal, frame: Frame);

    /**
     * 收到签复时
     *
     * @param channel 通道
     * @param frame   帧
     * @param stream  流
     */
    onReply(channel: ChannelInternal, frame: Frame, stream:StreamInternal<any> | null);


    /**
     * 关闭时
     *
     * @param channel 通道
     */
    onClose(channel: ChannelInternal);


    /**
     * 出错时
     *
     * @param channel 通道
     * @param error   错误信息
     */
    onError(channel: ChannelInternal, error: Error);


    /**
     * 执行关闭通知
     *
     * @param channel 通道
     */
    doCloseNotice(channel: ChannelInternal);
}