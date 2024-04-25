package org.noear.socketd.transport.core.impl;

import org.noear.socketd.exception.SocketDAlarmException;
import org.noear.socketd.exception.SocketDConnectionException;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.transport.stream.StreamInternal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 协议处理器默认实现
 *
 * @author noear
 * @since 2.0
 */
public class ProcessorDefault implements Processor {
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
     * 接收处理
     */
    public void onReceive(ChannelInternal channel, Frame frame) {
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
            channel.onOpenFuture((r, e) -> {
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
                        channel.sendPong();
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
                        StreamInternal stream = channel.getConfig().getStreamManger().getStream(frame.message().sid());
                        if (stream == null) {
                            onError(channel, exception);
                        } else {
                            channel.getConfig().getStreamManger().removeStream(frame.message().sid());
                            stream.onError(exception);
                        }
                        break;
                    }
                    case Flags.Pressure: //预留
                        break;
                    case Flags.Message:
                    case Flags.Request:
                    case Flags.Subscribe: {
                        onReceiveDo(channel, frame, false);
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
            channel.retrieve(frame, stream);
        } else {
            onMessage(channel, frame.message());
        }
    }


    /**
     * 打开时
     *
     * @param channel 通道
     */
    @Override
    public void onOpen(ChannelInternal channel) {
        channel.getConfig().getExchangeExecutor().submit(() -> {
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
     * @param message 消息
     */
    @Override
    public void onMessage(ChannelInternal channel, Message message) {
        channel.getConfig().getExchangeExecutor().submit(() -> {
            try {
                listener.onMessage(channel.getSession(), message);
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
     * 关闭时
     *
     * @param channel 通道
     */
    @Override
    public void onClose(ChannelInternal channel) {
        if (channel.isClosed() <= Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING) {
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
            listener.onClose(channel.getSession());
        } catch (Throwable error) {
            this.onError(channel, error);
        }
    }
}