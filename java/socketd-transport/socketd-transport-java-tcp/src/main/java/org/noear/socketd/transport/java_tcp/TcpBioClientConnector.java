package org.noear.socketd.transport.java_tcp;

import org.noear.socketd.exception.SocketdConnectionException;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Flag;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.internal.ChannelDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tcp-Bio 客户端连接器实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class TcpBioClientConnector extends ClientConnectorBase<TcpBioClient> {
    private static final Logger log = LoggerFactory.getLogger(TcpBioClientConnector.class);

    private Socket real;
    private Thread socketThread;

    public TcpBioClientConnector(TcpBioClient client) {
        super(client);
    }

    @Override
    public ChannelInternal connect() throws Exception {
        log.debug("Start connecting to: {}", client.config().getUrl());

        SocketAddress socketAddress = new InetSocketAddress(client.config().getHost(), client.config().getPort());

        //支持 ssl
        if (client.config().getSslContext() == null) {
            real = new Socket();
        } else {
            real = client.config().getSslContext().getSocketFactory().createSocket();
        }

        //闲置超时
        if (client.config().getIdleTimeout() > 0L) {
            //单位：毫秒
            real.setSoTimeout((int) client.config().getIdleTimeout());
        }

        if (client.config().getConnectTimeout() > 0) {
            real.connect(socketAddress, (int) client.config().getConnectTimeout());
        } else {
            real.connect(socketAddress);
        }

        CompletableFuture<ClientHandshakeResult> handshakeFuture = new CompletableFuture<>();

        try {
            ChannelInternal channel = new ChannelDefault<>(real, client.config(), client.assistant());

            socketThread = new Thread(() -> {
                try {
                    receive(channel, real, handshakeFuture);
                } catch (Throwable e) {
                    throw new IllegalStateException(e);
                }
            });

            socketThread.start();

            channel.sendConnect(client.config().getUrl());
        } catch (Throwable e) {
            log.debug("{}", e);
            close();
        }

        try {
            ClientHandshakeResult handshakeResult = handshakeFuture.get(client.config().getConnectTimeout(), TimeUnit.MILLISECONDS);

            if (handshakeResult.getException() != null) {
                throw handshakeResult.getException();
            } else {
                return handshakeResult.getChannel();
            }
        } catch (TimeoutException e) {
            close();
            throw new SocketdConnectionException("Connection timeout: " + client.config().getUrl());
        } catch (Exception e) {
            close();
            throw e;
        }
    }

    private void receive(ChannelInternal channel, Socket socket, CompletableFuture<ClientHandshakeResult> handshakeFuture) {
        while (true) {
            try {
                if (socket.isClosed()) {
                    client.processor().onClose(channel);
                    break;
                }

                Frame frame = client.assistant().read(socket);
                if (frame != null) {
                    client.processor().onReceive(channel, frame);

                    if (frame.getFlag() == Flag.Connack) {
                        handshakeFuture.complete(new ClientHandshakeResult(channel, null));
                    }
                }
            } catch (Exception e) {
                if (e instanceof SocketdConnectionException) {
                    //说明握手失败了
                    handshakeFuture.complete(new ClientHandshakeResult(channel, e));
                    break;
                }

                client.processor().onError(channel, e);

                if (e instanceof SocketException) {
                    break;
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (real == null) {
            return;
        }

        try {
            socketThread.interrupt();
            real.close();
        } catch (Throwable e) {
            log.debug("{}", e);
        }
    }
}