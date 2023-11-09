package org.noear.socketd.transport.core.impl;

import org.noear.socketd.exception.SocketdConnectionException;
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
            HandshakeInternal handshake = new HandshakeInternal(frame.getMessage());
            channel.setHandshake(handshake);

            //开始打开（可用于 url 签权）//禁止发消息
            onOpen(channel);
            channel.openConfirm();

            if (channel.isValid()) {
                //如果还有效，则发送链接确认
                channel.sendConnack(frame.getMessage(), true); //->Connack
            }
        } else if (frame.getFlag() == Flag.Connack) {
            //if client
            if("0".equals(frame.getMessage().getDataAsString())){
                //说明握手失败了
                throw new SocketdConnectionException("Connection request was rejected");
            }

            HandshakeInternal handshake = new HandshakeInternal(frame.getMessage());
            channel.setHandshake(handshake);

            onOpen(channel);
            channel.openConfirm();
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
                        onClose(channel);
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
                        onCloseInternal(channel);
                    }
                }
            } catch (Throwable e) {
                onError(channel, e);
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
            onMessage(channel, frame.getMessage());
        }
    }


    /**
     * 打开时
     *
     * @param channel 通道
     */
    @Override
    public void onOpen(Channel channel) throws IOException {
        listener.onOpen(channel.getSession());
    }

    /**
     * 收到消息时
     *
     * @param channel 通道
     * @param message 消息
     */
    @Override
    public void onMessage(Channel channel, Message message) throws IOException {
        listener.onMessage(channel.getSession(), message);
    }

    /**
     * 关闭时
     *
     * @param channel 通道
     */
    @Override
    public void onClose(Channel channel) {
        if (channel.isClosed() == false) {
            onCloseInternal(channel);
        }
    }

    /**
     * 关闭时（内部处理）
     *
     * @param channel 通道
     */
    private void onCloseInternal(Channel channel){
        listener.onClose(channel.getSession());
    }

    /**
     * 出错时
     *
     * @param channel 通道
     * @param error   错误信息
     */
    @Override
    public void onError(Channel channel, Throwable error) {
        listener.onError(channel.getSession(), error);
    }
}