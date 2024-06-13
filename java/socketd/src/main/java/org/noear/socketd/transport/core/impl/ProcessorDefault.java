package org.noear.socketd.transport.core.impl;

import org.noear.socketd.exception.SocketDAlarmException;
import org.noear.socketd.exception.SocketDConnectionException;
import org.noear.socketd.exception.SocketDException;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.entity.PressureEntity;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.stream.StreamInternal;
import org.noear.socketd.utils.IoCompletionHandler;
import org.noear.socketd.utils.MemoryUtils;
import org.noear.socketd.utils.RunnableEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.NotActiveException;

/**
 * 协议处理器默认实现（原则上，写不要在读的线程上执行）
 *
 * @author noear
 * @since 2.0
 */
public class ProcessorDefault implements Processor, FrameIoHandler {
    private static Logger log = LoggerFactory.getLogger(ProcessorDefault.class);

    private Listener listener = new SimpleListener();

    /**
     * 设置监听
     */
    @Override
    public void setListener(Listener listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }

    /**
     * 发送帧
     *
     * @param channel          通道
     * @param frame            帧
     * @param channelAssistant 通道助理
     * @param target           发送目标
     */
    @Override
    public <S> void sendFrame(ChannelInternal channel, Frame frame, ChannelAssistant<S> channelAssistant, S target) throws IOException {
        if (frame == null) {
            return;
        }

        if (channel.isValid() == false) {
            throw new NotActiveException("Channel is invalid");
        }

        IoCompletionHandlerImpl completionHandler = new IoCompletionHandlerImpl();

        //执行
        if (channel.getConfig().getTrafficLimiter() == null) {
            sendFrameHandle(channel, frame, channelAssistant, target, completionHandler);
        } else {
            channel.getConfig().getTrafficLimiter().sendFrame(this, channel, frame, channelAssistant, target, completionHandler);
        }

        //结果（如果是异步，就管不了）
        if (completionHandler.getThrowable() != null) {
            if (completionHandler.getThrowable() instanceof IOException) {
                throw (IOException) completionHandler.getThrowable();
            } else {
                throw new SocketDException("Channel send failure", completionHandler.getThrowable());
            }
        }
    }

    @Override
    public <S> void sendFrameHandle(ChannelInternal channel, Frame frame, ChannelAssistant<S> channelAssistant, S target, IoCompletionHandler completionHandler) {
        try {
            channelAssistant.write(target, frame, channel, completionHandler);

            if (frame.flag() >= Flags.Message) {
                listener.onSend(channel.getSession(), frame.message());
            }
        } catch (Throwable e) {
            completionHandler.completed(false, e);
        }
    }

    /**
     * 接收帧
     *
     * @param channel 通道
     * @param frame   帧
     */
    @Override
    public void reveFrame(ChannelInternal channel, Frame frame) {
        if (channel.getConfig().getTrafficLimiter() == null) {
            reveFrameHandle(channel, frame);
        } else {
            channel.getConfig().getTrafficLimiter().reveFrame(this, channel, frame);
        }
    }

    @Override
    public void reveFrameHandle(ChannelInternal channel, Frame frame) {
        if (log.isDebugEnabled()) {
            if (channel.getConfig().clientMode()) {
                log.debug("C-REV:{}", frame);
            } else {
                log.debug("S-REV:{}", frame);
            }
        }

        if (frame.flag() == Flags.Connect) {
            //if server
            HandshakeDefault handshake = new HandshakeDefault(frame.message());
            channel.setHandshake(handshake);

            //开始打开（可用于 url 签权）//禁止发消息
            channel.onOpenFuture((r, e) -> { //已经在异步线程内
                if (r) {
                    //如果无异常
                    if (channel.isValid()) {
                        //如果还有效，则发送链接确认
                        try {
                            channel.sendConnack(); //->Connack
                        } catch (Throwable err) {
                            onError(channel, err);
                        }
                    }
                } else {
                    //如果有异常
                    if (channel.isValid()) {
                        //如果还有效，则关闭通道
                        onCloseInternal(channel, Constants.CLOSE2001_ERROR);
                    }
                }
            });
            onOpen(channel);
        } else if (frame.flag() == Flags.Connack) {
            //if client
            HandshakeDefault handshake = new HandshakeDefault(frame.message());
            channel.setHandshake(handshake);

            onOpen(channel);
        } else {
            if (channel.getHandshake() == null) {
                channel.close(Constants.CLOSE1002_PROTOCOL_ILLEGAL);

                if (frame.flag() == Flags.Close) {
                    //说明握手失败了
                    throw new SocketDConnectionException("Connection request was rejected");
                }

                if (log.isWarnEnabled()) {
                    log.warn("{} channel handshake is null, sessionId={}",
                            channel.getConfig().getRoleName(),
                            channel.getSession().sessionId());
                }
                return;
            }

            //更新最后活动时间
            channel.setLiveTimeAsNow();

            try {
                switch (frame.flag()) {
                    case Flags.Ping: {
                        callAsync(channel, () -> channel.sendPong());
                        break;
                    }
                    case Flags.Pong: {
                        break;
                    }
                    case Flags.Close: {
                        //关闭通道
                        int code = 0;

                        if (frame.message() != null) {
                            code = frame.message().metaAsInt("code");
                        }

                        if (code == 0) {
                            code = Constants.CLOSE1001_PROTOCOL_CLOSE;
                        }

                        onCloseInternal(channel, code);
                        break;
                    }
                    case Flags.Alarm: {
                        //结束流，并异常通知
                        SocketDAlarmException exception = new SocketDAlarmException(frame.message());
                        channel.setAlarmCode(exception.getAlarmCode());

                        StreamInternal stream = channel.getStream(frame.message().sid());
                        if (stream == null) {
                            callAsync(channel, () -> onError(channel, exception));
                        } else {
                            channel.getConfig().getStreamManger().removeStream(frame.message().sid());
                            callAsync(channel, () -> stream.onError(exception));
                        }
                        break;
                    }
                    case Flags.Pressure: {
                        int code = frame.message().metaAsInt("code");
                        channel.setAlarmCode(code);
                        break;
                    }
                    case Flags.Message:
                    case Flags.Request:
                    case Flags.Subscribe: {
                        if (chkMemoryLimit(channel, frame)) {
                            onReceiveDo(channel, frame, false);
                        }
                        break;
                    }
                    case Flags.Reply:
                    case Flags.ReplyEnd: {
                        onReceiveDo(channel, frame, true);
                        break;
                    }
                    default: {
                        onCloseInternal(channel, Constants.CLOSE1002_PROTOCOL_ILLEGAL);
                    }
                }
            } catch (Throwable e) {
                onError(channel, e);
            }
        }
    }

    /**
     * 检测内存限制（跨语方不方便迁移时略过）
     *
     * @return 是否通过
     */
    private boolean chkMemoryLimit(ChannelInternal channel, Frame frame) {
        if (channel.getConfig().useMaxMemoryLimit()) {
            float useMemoryRatio = MemoryUtils.getUseMemoryRatio();

            if (useMemoryRatio > channel.getConfig().getMaxMemoryRatio()) {
                if (frame.message().meta(EntityMetas.META_X_UNLIMITED) == null) {
                    //限制流量
                    try {
                        String alarm = String.format(" memory usage over limit: %.2f%%", useMemoryRatio * 100);

                        if (log.isDebugEnabled()) {
                            log.debug("Local " + alarm + ", frame: " + frame);
                        }

                        PressureEntity pressure = new PressureEntity(channel.getConfig().getRoleName() + alarm);
                        channel.sendAlarm(frame.message(), pressure);
                    } catch (Throwable e) {
                        onError(channel, e);
                    }

                    return false;
                } else {
                    //不限制流量
                    return true;
                }
            }
        }

        return true;
    }

    private void onReceiveDo(ChannelInternal channel, Frame frame, boolean isReply) throws IOException {
        StreamInternal stream = null;
        int streamIndex = 0;
        int streamTotal = 1;

        if (isReply) {
            stream = channel.getStream(frame.message().sid());
        }

        //如果启用了聚合!
        if (channel.getConfig().getFragmentHandler().aggrEnable()) {
            //尝试聚合分片处理
            String fragmentIdxStr = frame.message().meta(EntityMetas.META_DATA_FRAGMENT_IDX);
            if (fragmentIdxStr != null) {
                //解析分片索引
                streamIndex = Integer.parseInt(fragmentIdxStr);
                Frame frameNew = channel.getConfig().getFragmentHandler().aggrFragment(channel, streamIndex, frame.message());

                if (stream != null) {
                    //解析分片总数
                    streamTotal = Integer.parseInt(frame.message().metaOrDefault(EntityMetas.META_DATA_FRAGMENT_TOTAL, "0"));
                }

                if (frameNew == null) {
                    if (stream != null) {
                        stream.onProgress(false, streamIndex, streamTotal);
                    }
                    return;
                } else {
                    frame = frameNew;
                }
            }
        }

        //执行接收处理
        if (isReply) {
            if (stream != null) {
                stream.onProgress(false, streamIndex, streamTotal);
            }
            onReply(channel, frame, stream);
        } else {
            onMessage(channel, frame);
        }
    }


    /**
     * 打开时
     *
     * @param channel 通道
     */
    @Override
    public void onOpen(ChannelInternal channel) {
        callAsync(channel, () -> {
            try {
                listener.onOpen(channel.getSession());
                channel.doOpenFuture(true, null);
            } catch (Throwable e) {
                if (log.isWarnEnabled()) {
                    log.warn("{} channel listener onOpen error",
                            channel.getConfig().getRoleName(), e);
                }
                channel.doOpenFuture(false, e);
            }
        });
    }

    /**
     * 收到消息时
     *
     * @param channel 通道
     * @param frame   帧
     */
    @Override
    public void onMessage(ChannelInternal channel, Frame frame) {
        callAsync(channel, () -> {
            try {
                listener.onMessage(channel.getSession(), frame.message());
            } catch (Throwable e) {
                if (log.isWarnEnabled()) {
                    log.warn("{} channel listener onMessage error",
                            channel.getConfig().getRoleName(), e);
                }
                onError(channel, e);
            }
        });
    }

    /**
     * 收到签复时
     *
     * @param channel 通道
     * @param frame   帧
     * @param stream  流
     */
    @Override
    public void onReply(ChannelInternal channel, Frame frame, StreamInternal stream) {
        if (stream != null) {
            if (stream.demands() < Constants.DEMANDS_MULTIPLE || frame.flag() == Flags.ReplyEnd) {
                //如果是单收或者答复结束，则移除流接收器
                channel.getConfig().getStreamManger().removeStream(frame.message().sid());
            }

            if(stream.demands() < Constants.DEMANDS_MULTIPLE){
                //单需（内部已是异步）//再换线程会让 await 失效
                stream.onReply(frame.message());
            }

            callAsync(channel, () -> {
                if(stream.demands() == Constants.DEMANDS_MULTIPLE) {
                    //多需
                    stream.onReply(frame.message());
                }
                listener.onReply(channel.getSession(), frame.message());
            });
        } else {
            callAsync(channel, () -> {
                listener.onReply(channel.getSession(), frame.message());
            });

            if (log.isDebugEnabled()) {
                log.debug("{} stream not found, sid={}, sessionId={}",
                        channel.getConfig().getRoleName(), frame.message().sid(), channel.getSession().sessionId());
            }
        }
    }

    /**
     * 关闭时
     *
     * @param channel 通道
     */
    @Override
    public void onClose(ChannelInternal channel) {
        if (channel.closeCode() <= Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING) {
            onCloseInternal(channel, Constants.CLOSE2003_DISCONNECTION);
        }
    }

    /**
     * 关闭时（内部处理）
     *
     * @param channel 通道
     */
    private void onCloseInternal(ChannelInternal channel, int code) {
        channel.close(code);
    }

    /**
     * 出错时
     *
     * @param channel 通道
     * @param error   错误信息
     */
    @Override
    public void onError(ChannelInternal channel, Throwable error) {
        try {
            listener.onError(channel.getSession(), error);
        } catch (Throwable e) {
            if (log.isWarnEnabled()) {
                log.warn("{} channel listener onError error",
                        channel.getConfig().getRoleName(), e);
            }
        }
    }

    /**
     * 执行关闭通知
     *
     * @param channel 通道
     */
    public void doCloseNotice(ChannelInternal channel) {
        try {
            if (channel.getHandshake() != null) {
                //如果没有 handshake 成功，不需要通知了
                listener.onClose(channel.getSession());
            }
        } catch (Throwable error) {
            this.onError(channel, error);
        }
    }

    private void callAsync(ChannelInternal channel, RunnableEx<Throwable> runnable) {
        try {
            channel.getConfig().getWorkExecutor().submit(() -> {
                try {
                    runnable.run();
                } catch (Throwable e) {
                    onError(channel, e);
                }
            });
        } catch (Throwable e) {
            onError(channel, e);
        }
    }
}