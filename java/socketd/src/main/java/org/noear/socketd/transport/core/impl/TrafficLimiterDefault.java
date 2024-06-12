package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.utils.IoCompletionHandler;

/**
 * 流量限制器默认实现
 *
 * @author noear
 * @since 2.5
 */
public class TrafficLimiterDefault implements TrafficLimiter {
    private int sendRate;
    private int receRate;
    private final long interval = 1000L;

    public int getSendRate() {
        return sendRate;
    }

    public void setSendRate(int sendRate) {
        this.sendRate = sendRate;
    }

    public int getReceRate() {
        return receRate;
    }

    public void setReceRate(int receRate) {
        this.receRate = receRate;
    }

    private int sendCount;
    private int receCount;
    private long sendLatestLimitTime; // 发送数据限流重置时间
    private long receLatestLimitTime; // 接收数据限流重置时间


    private long receLatestTime; // 最后接收时间
    private long sendLatestTime; // 最后发送时间

    public TrafficLimiterDefault(int sendAndReceRate) {
        this(sendAndReceRate, sendAndReceRate);
    }

    public TrafficLimiterDefault(int sendRate, int receRate) {
        this.sendRate = sendRate;
        this.receRate = receRate;
    }

    /**
     * 发送帧（在写锁范围，才有效）
     *
     * @param frameIoHandler   帧输入输出处理
     * @param channel          通道
     * @param frame            帧
     * @param channelAssistant 通道助理
     * @param target           发送目标
     */
    @Override
    public <S> void sendFrame(FrameIoHandler frameIoHandler, ChannelInternal channel, Frame frame, ChannelAssistant<S> channelAssistant, S target, IoCompletionHandler completionHandler) {
        if (sendRate < 1) {
            //没有限制
            frameIoHandler.sendFrameHandle(channel, frame, channelAssistant, target, completionHandler);
            return;
        }
        /*
        限流逻辑：（只能是单线程场景这样处理）
            首次进入不限流, 并重置计数与下次限制时间
            记数达到上限时判断是否超过限流重置时间，
                如果超过就从新计数并更新时间（速率小于限制，不做限流）
                否则就计算剩余等待时间休眠线程
         */
        if (sendLatestTime >= sendLatestLimitTime) {
            //超过间隔重置时间
            sendCount = 0;
            sendLatestLimitTime = System.currentTimeMillis() + interval; // 更新下次重置时间
        }

        sendCount++;
        if (sendCount < sendRate) {
            frameIoHandler.sendFrameHandle(channel, frame, channelAssistant, target, completionHandler);
        } else {
            sendLatestTime = System.currentTimeMillis(); // 到达限制了 记录最后时间
            if (sendLatestTime < sendLatestLimitTime){
                try {
                    // 如果太快，则等待一下
                    Thread.sleep(sendLatestLimitTime - sendLatestTime);
                } catch (Throwable e) {
                    return;
                }
                
                sendFrame(frameIoHandler, channel, frame, channelAssistant, target, completionHandler);
            }
        }
    }

    /**
     * 接收帧（在读线程里，才有效）
     *
     * @param frameIoHandler 帧输入输出处理
     * @param channel        通道
     * @param frame          帧
     */
    @Override
    public void reveFrame(FrameIoHandler frameIoHandler, ChannelInternal channel, Frame frame) {
        if (receRate < 1) {
            //没有限制
            frameIoHandler.reveFrameHandle(channel, frame);
            return;
        }
        /*
        限流逻辑：（只能是单线程场景这样处理）
            首次进入不限流, 并重置计数与下次限制时间
            记数达到上限时判断是否超过限流重置时间，
                如果超过就从新计数并更新时间（速率小于限制，不做限流）
                否则就计算剩余等待时间休眠线程
         */
        if (receLatestTime >= receLatestLimitTime) {
            //超过间隔重置时间
            receCount = 0;
            receLatestLimitTime = System.currentTimeMillis() + interval; // 更新下次重置时间
        }

        receCount++;
        if (receCount < receRate) {
            frameIoHandler.reveFrameHandle(channel, frame);
        } else {
            receLatestTime = System.currentTimeMillis(); // 到达限制了 记录最后时间
            if (receLatestTime < receLatestLimitTime){
                try {
                    // 如果太快，则等待一下
                    Thread.sleep(receLatestLimitTime - receLatestTime);
                } catch (Throwable e) {
                    return;
                }
                
                reveFrame(frameIoHandler, channel, frame);
            }
        }
    }
}