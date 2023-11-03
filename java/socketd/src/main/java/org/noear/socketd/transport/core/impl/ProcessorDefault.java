package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 处理器默认实现
 *
 * @author noear
 * @since 2.0
 */
public class ProcessorDefault implements Processor {
    private static Logger log = LoggerFactory.getLogger(ProcessorDefault.class);

    private Listener listener = new SimpleListener();


    @Override
    public void setListener(Listener listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }

    public void onReceive(Channel channel, Frame frame) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("{}", frame);
        }

        if (frame.getFlag() == Flag.Connect) {
            //if server
            Message connectMessage = frame.getMessage();
            channel.setHandshaker(new Handshaker(connectMessage));
            channel.sendConnack(connectMessage); //->Connack

            onOpen(channel.getSession());
        } else if (frame.getFlag() == Flag.Connack) {
            //if client
            Message message = frame.getMessage();
            channel.setHandshaker(new Handshaker(message));

            onOpen(channel.getSession());
        } else {
            if (channel.getHandshaker() == null) {
                channel.close();
                if (log.isWarnEnabled()) {
                    log.warn("Channel handshaker is null, sessionId={}", channel.getSession().getSessionId());
                }
                return;
            }

            channel.setLiveTime();

            try {
                switch (frame.getFlag()) {
                    case Ping: {
                        channel.sendPong();
                        break;
                    }
                    case Pong: {
                        break;
                    }
                    case Close: {
                        channel.close();
                        onClose(channel.getSession());
                        break;
                    }
                    case Message:
                    case Request:
                    case Subscribe: {
                        onReceiveDo(channel, frame, false);
                        break;
                    }
                    case Reply:
                    case ReplyEnd: {
                        onReceiveDo(channel, frame, true);
                        break;
                    }
                    default: {
                        channel.close();
                        onClose(channel.getSession());
                    }
                }
            } catch (Throwable e) {
                onError(channel.getSession(), e);
            }
        }
    }

    private void onReceiveDo(Channel channel, Frame frame, boolean isReply) throws IOException {
        String rangeIdxStr = frame.getMessage().getEntity().getMeta(Constants.META_DATA_RANGE_IDX);
        if (rangeIdxStr != null) {
            RangesFrame rangesFrame = channel.getConfig().getRangesHandler().aggrRanges(channel, frame);

            if (rangesFrame == null) {
                return;
            } else {
                frame = rangesFrame.getFrame();
            }
        }

        if (isReply) {
            channel.retrieve(frame);
        } else {
            onMessage(channel.getSession(), frame.getMessage());
        }
    }


    /**
     * 打开时
     *
     * @param session 会话
     */
    @Override
    public void onOpen(Session session) throws IOException {
        listener.onOpen(session);
    }

    /**
     * 收到消息时
     *
     * @param session 会话
     * @param message 消息
     */
    @Override
    public void onMessage(Session session, Message message) throws IOException {
        listener.onMessage(session, message);
    }

    /**
     * 关闭时
     *
     * @param session 会话
     */
    @Override
    public void onClose(Session session) {
        listener.onClose(session);
    }

    /**
     * 出错时
     *
     * @param session 会话
     * @param error   错误信息
     */
    @Override
    public void onError(Session session, Throwable error) {
        listener.onError(session, error);
    }
}