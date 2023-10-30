package org.noear.socketd.broker.smartsocket;

import org.noear.socketd.client.ClientConnectorBase;
import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.Flag;
import org.noear.socketd.protocol.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Aio 客户端连接器实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpAioClientConnector extends ClientConnectorBase<TcpAioClient> implements MessageProcessor<Frame> {
    private static final Logger log = LoggerFactory.getLogger(TcpAioClientConnector.class);

    private AioQuickClient real;
    private CompletableFuture<Channel> future;

    public TcpAioClientConnector(TcpAioClient client) {
        super(client);
    }

    @Override
    public Channel connect() throws IOException {
        real = new AioQuickClient(client.uri().getHost(), client.uri().getPort(), client.exchanger(), this);
        if (client.config().getReadBufferSize() > 0) {
            real.setReadBufferSize(client.config().getReadBufferSize());
        }

        if (client.config().getWriteBufferSize() > 0) {
            real.setWriteBuffer(client.config().getWriteBufferSize(), 16);
        }

        if (client.config().getConnectTimeout() > 0) {
            real.connectTimeout((int) client.config().getConnectTimeout());
        }

        future = new CompletableFuture<>();
        real.start();

        try {
            return future.get(client.config().getConnectTimeout(), TimeUnit.MILLISECONDS);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (real == null) {
            return;
        }

        try {
            real.shutdown();
        } catch (Throwable e) {
            log.debug("{}", e);
        }
    }

    @Override
    public void process(AioSession s, Frame frame) {
        Channel channel = Attachment.getChannel(s, client.exchanger());

        try {
            client.processor().onReceive(channel, frame);

            if (frame.getFlag() == Flag.Connack) {
                future.complete(channel);
            }
        } catch (Throwable e) {
            if (channel == null) {
                log.warn(e.getMessage(), e);
            } else {
                client.processor().onError(channel.getSession(), e);
            }
        }
    }

    @Override
    public void stateEvent(AioSession s, StateMachineEnum state, Throwable e) {
        switch (state) {
            case NEW_SESSION: {
                Channel channel = Attachment.getChannel(s, client.exchanger());
                try {
                    channel.sendConnect(client.url());
                } catch (Throwable ex) {
                    client.processor().onError(channel.getSession(), ex);
                }
            }
            break;

            case SESSION_CLOSED:
                client.processor().onClose(Attachment.getChannel(s, client.exchanger()).getSession());
                break;

            case PROCESS_EXCEPTION:
            case DECODE_EXCEPTION:
            case INPUT_EXCEPTION:
            case ACCEPT_EXCEPTION:
            case OUTPUT_EXCEPTION:
                client.processor().onError(Attachment.getChannel(s, client.exchanger()).getSession(), e);
                break;
        }
    }
}