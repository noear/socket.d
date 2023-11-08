package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.listener.SimpleListener;
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
    public void onReceive(Channel channel, Frame frame) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("{}", frame);
        }

        if (frame.getFlag() == Flag.Connect) {
            //if server
            Message connectMessage = frame.getMessage();
            channel.setHandshake(new Handshake(connectMessage));

            //开始打开（可用于 url 签权）//禁止发消息
            onOpen(channel.getSession());

            if (channel.isValid()) {
                //如果还有效，则发送链接确认
                channel.sendConnack(connectMessage); //->Connack
            }
        } else if (frame.getFlag() == Flag.Connack) {
            //if client
            Message message = frame.getMessage();
            channel.setHandshake(new Handshake(message));

            onOpen(channel.getSession());
        } else {
            if (channel.getHandshake() == null) {
                channel.close();
                if (log.isWarnEnabled()) {
                    log.warn("Channel andshake is null, sessionId={}", channel.getSession().getSessionId());
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
        //尝试分片处理
        String fragmentIdxStr = frame.getMessage().getMeta(EntityMetas.META_DATA_FRAGMENT_IDX);
        if (fragmentIdxStr != null) {
            //解析分片索引
            int index = Integer.parseInt(fragmentIdxStr);
            Frame frameNew = channel.getConfig().getFragmentHandler().aggrFragment(channel, index, frame);

            if (frameNew == null) {
                return;
            } else {
                frame = frameNew;
            }
        }

        //执行接收处理
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