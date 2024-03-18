package org.noear.socketd.transport.smartsocket.tcp.impl;

import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.smartsocket.tcp.TcpAioServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.extension.processor.AbstractMessageProcessor;
import org.smartboot.socket.transport.AioSession;

/**
 * 服务端消息处理器
 *
 * @author noear
 * @since 2.0
 */
public class ServerMessageProcessor extends AbstractMessageProcessor<Frame> {
    private static final Logger log = LoggerFactory.getLogger(ServerMessageProcessor.class);

    private TcpAioServer server;

    public ServerMessageProcessor(TcpAioServer server) {
        this.server = server;
    }


    @Override
    public void process0(AioSession s, Frame frame) {
        ChannelInternal channel = s.getAttachment();

        try {
            server.getProcessor().onReceive(channel, frame);
        } catch (Throwable e) {
            if (channel == null) {
                log.warn("Server process0 error", e);
            } else {
                server.getProcessor().onError(channel, e);
            }
        }
    }

    @Override
    public void stateEvent0(AioSession s, StateMachineEnum state, Throwable e) {
        switch (state) {
            case NEW_SESSION:
                s.setAttachment(new ChannelDefaultEx<>(s, server));
                break;

            case SESSION_CLOSED: {
                ChannelDefaultEx c = s.getAttachment();
                server.getProcessor().onClose(c);
                break;
            }

            case PROCESS_EXCEPTION:
            case DECODE_EXCEPTION:
            case INPUT_EXCEPTION:
            case ACCEPT_EXCEPTION:
            case OUTPUT_EXCEPTION: {
                ChannelDefaultEx c = s.getAttachment();
                if (c != null) {
                    server.getProcessor().onError(c, e);
                }
                break;
            }
        }
    }
}
