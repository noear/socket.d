package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.utils.IoCompletionHandler;

/**
 * @author noear
 * @since 2.5
 */
public class TrafficLimiterDefault implements TrafficLimiter {
    private int sendRate;
    private int receRate;
    private int interval;

    private int sendCount;
    private int receCount;
    private long sendLatestLimitTime;
    private long receLatestLimitTime;

    public TrafficLimiterDefault(int sendAndReceRate) {
        this(sendAndReceRate, sendAndReceRate, 1000);
    }


    public TrafficLimiterDefault(int sendRate, int receRate) {
        this(sendRate, receRate, 1000);
    }

    public TrafficLimiterDefault(int sendRate, int receRate, int interval) {
        this.sendRate = sendRate;
        this.receRate = receRate;
        this.interval = interval;
    }

    @Override
    public <S> void sendFrame(FrameIoHandler frameIoHandler, ChannelInternal channel, Frame frame, ChannelAssistant<S> channelAssistant, S target, IoCompletionHandler completionHandler) {
        if (sendRate < 1) {
            //没有限制
            frameIoHandler.sendFrameHandle(channel, frame, channelAssistant, target, completionHandler);
            return;
        }

        long timespan = System.currentTimeMillis() - sendLatestLimitTime;
        if (timespan > interval) {
            //超过间隔重置时间
            sendCount = 0;
            sendLatestLimitTime = System.currentTimeMillis();
        }

        sendCount++;
        if (sendCount < sendRate) {
            frameIoHandler.sendFrameHandle(channel, frame, channelAssistant, target, completionHandler);
        } else {
            try {
                //或者转 ScheduledExecutorService 延后处理
                Thread.sleep(10);
            } catch (Throwable e) {
                return;
            }

            sendFrame(frameIoHandler, channel, frame, channelAssistant, target, completionHandler);
        }
    }

    @Override
    public void reveFrame(FrameIoHandler frameIoHandler, ChannelInternal channel, Frame frame) {
        if (receRate < 1) {
            //没有限制
            frameIoHandler.reveFrameHandle(channel, frame);
            return;
        }

        long timespan = System.currentTimeMillis() - receLatestLimitTime;
        if (timespan > interval) {
            //超过间隔重置时间
            receCount = 0;
            receLatestLimitTime = System.currentTimeMillis();
        }

        receCount++;
        if (receCount < receRate) {
            frameIoHandler.reveFrameHandle(channel, frame);
        } else {
            try {
                //或者转 ScheduledExecutorService 延后处理
                Thread.sleep(10);
            } catch (Throwable e) {
                return;
            }

            reveFrame(frameIoHandler, channel, frame);
        }
    }
}