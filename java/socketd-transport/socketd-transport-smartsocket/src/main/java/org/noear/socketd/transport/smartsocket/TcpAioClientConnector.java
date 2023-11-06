package org.noear.socketd.transport.smartsocket;

import org.noear.socketd.transport.smartsocket.impl.Attachment;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.exception.SocketdTimeoutException;
import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.Flag;
import org.noear.socketd.transport.core.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.NetMonitor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.extension.plugins.SslPlugin;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tcp-Aio 客户端连接器实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class TcpAioClientConnector extends ClientConnectorBase<TcpAioClient> implements MessageProcessor<Frame>, NetMonitor {
    private static final Logger log = LoggerFactory.getLogger(TcpAioClientConnector.class);

    private AioQuickClient real;
    private CompletableFuture<Channel> future;
    private SslPlugin<Integer> sslPlugin;

    public TcpAioClientConnector(TcpAioClient client) {
        super(client);
    }

    @Override
    public Channel connect() throws Exception {
        log.debug("Start connecting to: {}", client.config().getUrl());

        real = new AioQuickClient(client.config().getHost(), client.config().getPort(), client.assistant(), this);

        //支持 ssl
        if (client.config().getSslContext() != null) {
            sslPlugin = new SslPlugin<>(client.config()::getSslContext, sslEngine -> {
                sslEngine.setUseClientMode(true);
            });
        }

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
        } catch (TimeoutException e) {
            close();
            throw new SocketdTimeoutException("Connection timeout: " + client.config().getUrl());
        } catch (Exception e) {
            close();
            throw e;
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

    private Channel getChannel(AioSession s) {
        return Attachment.getChannel(s, client.config(), client.assistant());
    }

    @Override
    public void process(AioSession s, Frame frame) {
        Channel channel = getChannel(s);

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

    @Override
    public AsynchronousSocketChannel shouldAccept(AsynchronousSocketChannel asynchronousSocketChannel) {
        if (sslPlugin == null) {
            return asynchronousSocketChannel;
        } else {
            return sslPlugin.shouldAccept(asynchronousSocketChannel);
        }
    }

    @Override
    public void afterRead(AioSession aioSession, int i) {

    }

    @Override
    public void beforeRead(AioSession aioSession) {

    }

    @Override
    public void afterWrite(AioSession aioSession, int i) {

    }

    @Override
    public void beforeWrite(AioSession aioSession) {

    }
}