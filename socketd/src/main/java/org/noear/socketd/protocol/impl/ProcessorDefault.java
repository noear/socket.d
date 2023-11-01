package org.noear.socketd.protocol.impl;

import org.noear.socketd.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 处理器
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
            Message message = frame.getMessage();
            channel.setHandshaker(new Handshaker(message));
            channel.sendConnack(message); //->Connack

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
                    case Message: {
                        onMessage(channel.getSession(), frame.getMessage());
                        break;
                    }
                    case Request: {
                        onMessage(channel.getSession(), frame.getMessage());
                        break;
                    }
                    case Subscribe: {
                        onMessage(channel.getSession(), frame.getMessage());
                        break;
                    }
                    case Reply:
                    case ReplyEnd:{
                        channel.retrieve(frame);
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


    @Override
    public void onOpen(Session session) throws IOException{
        listener.onOpen(session);
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        listener.onMessage(session, message);
    }

    @Override
    public void onClose(Session session) {
        listener.onClose(session);
    }

    @Override
    public void onError(Session session, Throwable error) {
        listener.onError(session, error);
    }
}