package org.noear.socketd.transport.smartsocket;

import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.exception.SocketdTimeoutException;
import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.smartsocket.impl.ClientMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.socket.extension.plugins.IdleStatePlugin;
import org.smartboot.socket.extension.plugins.SslPlugin;
import org.smartboot.socket.transport.AioQuickClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tcp-Aio 客户端连接器实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class TcpAioClientConnector extends ClientConnectorBase<TcpAioClient> {
    private static final Logger log = LoggerFactory.getLogger(TcpAioClientConnector.class);

    private AioQuickClient real;

    public TcpAioClientConnector(TcpAioClient client) {
        super(client);
    }

    @Override
    public Channel connect() throws Exception {
        log.debug("Start connecting to: {}", client.config().getUrl());

        ClientMessageProcessor processor = new ClientMessageProcessor(client);

        //支持 ssl
        if (client.config().getSslContext() != null) {
           SslPlugin<Frame>  sslPlugin = new SslPlugin<>(client.config()::getSslContext, sslEngine -> {
                sslEngine.setUseClientMode(true);
            });

            processor.addPlugin(sslPlugin);
        }

        //闲置超时
        if(client.config().getIdleTimeout() > 0) {
            processor.addPlugin(new IdleStatePlugin<>((int) client.config().getIdleTimeout(), true, false));
        }


        real = new AioQuickClient(client.config().getHost(), client.config().getPort(), client.assistant(), processor);


        if (client.config().getReadBufferSize() > 0) {
            real.setReadBufferSize(client.config().getReadBufferSize());
        }

        if (client.config().getWriteBufferSize() > 0) {
            real.setWriteBuffer(client.config().getWriteBufferSize(), 16);
        }

        if (client.config().getConnectTimeout() > 0) {
            real.connectTimeout((int) client.config().getConnectTimeout());
        }

        real.start();

        try {
            return processor.getChannelFuture().get(client.config().getConnectTimeout(), TimeUnit.MILLISECONDS);
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
}