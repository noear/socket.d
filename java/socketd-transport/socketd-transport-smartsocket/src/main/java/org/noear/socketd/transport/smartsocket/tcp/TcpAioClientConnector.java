package org.noear.socketd.transport.smartsocket.tcp;

import org.noear.socketd.exception.SocketDConnectionException;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.smartsocket.tcp.impl.ClientMessageProcessor;
import org.noear.socketd.utils.RunUtils;
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
    public ChannelInternal connect() throws IOException {
        //关闭之前的资源
        close();

        ClientMessageProcessor messageProcessor = new ClientMessageProcessor(client);


        RunUtils.async(() -> {
            try {
                connectDo(messageProcessor);
            } catch (Throwable e) {
                messageProcessor.getHandshakeFuture().complete(new ClientHandshakeResult(null, e));
            }
        });

        try {
            //等待握手结果
            ClientHandshakeResult handshakeResult = messageProcessor.getHandshakeFuture().get(client.getConfig().getConnectTimeout(), TimeUnit.MILLISECONDS);

            if (handshakeResult.getThrowable() != null) {
                throw handshakeResult.getThrowable();
            } else {
                return handshakeResult.getChannel();
            }
        } catch (TimeoutException e) {
            close();
            throw new SocketDConnectionException("Connection timeout: " + client.getConfig().getLinkUrl());
        } catch (Throwable e) {
            close();

            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new SocketDConnectionException("Connection failed: " + client.getConfig().getLinkUrl(), e);
            }
        }
    }

    private void connectDo(ClientMessageProcessor messageProcessor) throws Exception {
        //支持 ssl
        if (client.getConfig().getSslContext() != null) {
            SslPlugin<Frame> sslPlugin = new SslPlugin<>(client.getConfig()::getSslContext, sslEngine -> {
                sslEngine.setUseClientMode(true);
            });

            messageProcessor.addPlugin(sslPlugin);
        }

        //闲置超时
        if (client.getConfig().getIdleTimeout() > 0) {
            messageProcessor.addPlugin(new IdleStatePlugin<>((int) client.getConfig().getIdleTimeout(), true, false));
        }


        real = new AioQuickClient(client.getConfig().getHost(), client.getConfig().getPort(), client.frameProtocol(), messageProcessor);

        if (client.getConfig().getReadBufferSize() > 0) {
            real.setReadBufferSize(client.getConfig().getReadBufferSize());
        }

        if (client.getConfig().getWriteBufferSize() > 0) {
            real.setWriteBuffer(client.getConfig().getWriteBufferSize(), 16);
        }

        if (client.getConfig().getConnectTimeout() > 0) {
            real.connectTimeout((int) client.getConfig().getConnectTimeout());
        }

        real.start();
    }

    @Override
    public void close() {
        try {
            if (real != null) {
                real.shutdown();
            }
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug("Client connector close error", e);
            }
        }
    }
}