package org.noear.socketd.transport.smartsocket.impl;

import org.noear.socketd.exception.SocketdConnectionException;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Flags;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.smartsocket.TcpAioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.extension.processor.AbstractMessageProcessor;
import org.smartboot.socket.transport.AioSession;

import java.util.concurrent.CompletableFuture;

/**
 * 客户端消息处理器
 *
 * @author noear
 * @since 2.0
 */
public class ClientMessageProcessor extends AbstractMessageProcessor<Frame> {
    private static final Logger log = LoggerFactory.getLogger(ClientMessageProcessor.class);
    private TcpAioClient client;
    private CompletableFuture<ClientHandshakeResult> handshakeFuture = new CompletableFuture<>();

    public ClientMessageProcessor(TcpAioClient client) {
        this.client = client;
    }

    public CompletableFuture<ClientHandshakeResult> getHandshakeFuture() {
        return handshakeFuture;
    }

    private ChannelDefaultEx getChannel(AioSession s) {
        return ChannelDefaultEx.get(s, client);
    }

    @Override
    public void process0(AioSession s, Frame frame) {
        ChannelInternal channel = getChannel(s);

        try {
            if (frame.getFlag() == Flags.Connack) {
                channel.onOpenFuture().whenComplete((r, e) -> {
                    handshakeFuture.complete(new ClientHandshakeResult(channel, e));
                });
            }

            client.processor().onReceive(channel, frame);
        } catch (Exception e) {
            if (e instanceof SocketdConnectionException) {
                //说明握手失败了
                handshakeFuture.complete(new ClientHandshakeResult(channel, e));
                return;
            }

            if (channel == null) {
                log.warn("Client process0 error", e);
            } else {
                client.processor().onError(channel, e);
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
                    client.processor().onError(channel, ex);
                }
            }
            break;

            case SESSION_CLOSED:
                client.processor().onClose(getChannel(s));
                break;

            case PROCESS_EXCEPTION:
            case DECODE_EXCEPTION:
            case INPUT_EXCEPTION:
            case ACCEPT_EXCEPTION:
            case OUTPUT_EXCEPTION:
                client.processor().onError(getChannel(s), e);
                break;
        }
    }
}
