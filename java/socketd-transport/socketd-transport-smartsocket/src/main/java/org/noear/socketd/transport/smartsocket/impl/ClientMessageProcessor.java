package org.noear.socketd.transport.smartsocket.impl;

import org.noear.socketd.exception.SocketdConnectionException;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.Flag;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.smartsocket.TcpAioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.extension.processor.AbstractMessageProcessor;
import org.smartboot.socket.transport.AioSession;

import java.util.concurrent.CompletableFuture;

/**
 * @author noear
 * @since 2.0
 */
public class ClientMessageProcessor extends AbstractMessageProcessor<Frame> {
    private static final Logger log = LoggerFactory.getLogger(ClientMessageProcessor.class);
    private TcpAioClient client;
    private CompletableFuture<ClientHandshakeResult> handshakeFuture = new CompletableFuture<>();
    public ClientMessageProcessor(TcpAioClient client){
        this.client = client;
    }

    public CompletableFuture<ClientHandshakeResult> getHandshakeFuture() {
        return handshakeFuture;
    }

    private Channel getChannel(AioSession s) {
        return Attachment.getChannel(s, client.config(), client.assistant());
    }

    @Override
    public void process0(AioSession s, Frame frame) {
        Channel channel = getChannel(s);

        try {
            client.processor().onReceive(channel, frame);

            if (frame.getFlag() == Flag.Connack) {
                handshakeFuture.complete(new ClientHandshakeResult(channel, null));
            }
        } catch (Exception e) {
            if (e instanceof SocketdConnectionException) {
                //说明握手失败了
                handshakeFuture.complete(new ClientHandshakeResult(channel, e));
                return;
            }

            if (channel == null) {
                log.warn(e.getMessage(), e);
            } else {
                client.processor().onError(channel.getSession(), e);
            }
        }
    }

    @Override
    public void stateEvent0(AioSession s, StateMachineEnum state, Throwable e) {
        switch (state) {
            case NEW_SESSION: {
                Channel channel = getChannel(s);
                try {
                    channel.sendConnect(client.config().getUrl());
                } catch (Throwable ex) {
                    client.processor().onError(channel.getSession(), ex);
                }
            }
            break;

            case SESSION_CLOSED:
                client.processor().onClose(getChannel(s).getSession());
                break;

            case PROCESS_EXCEPTION:
            case DECODE_EXCEPTION:
            case INPUT_EXCEPTION:
            case ACCEPT_EXCEPTION:
            case OUTPUT_EXCEPTION:
                client.processor().onError(getChannel(s).getSession(), e);
                break;
        }
    }
}
