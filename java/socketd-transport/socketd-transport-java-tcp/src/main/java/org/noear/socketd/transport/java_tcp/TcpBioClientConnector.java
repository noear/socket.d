package org.noear.socketd.transport.java_tcp;

import org.noear.socketd.exception.SocketdConnectionException;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Flags;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.internal.ChannelDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.*;

/**
 * Tcp-Bio 客户端连接器实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class TcpBioClientConnector extends ClientConnectorBase<TcpBioClient> {
    private static final Logger log = LoggerFactory.getLogger(TcpBioClientConnector.class);

    private Socket real;
    private Thread clientThread;

    public TcpBioClientConnector(TcpBioClient client) {
        super(client);
    }

    @Override
    public ChannelInternal connect() throws IOException {
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

        //读缓冲大小
        if (client.config().getReadBufferSize() > 0) {
            real.setReceiveBufferSize(client.config().getReadBufferSize());
        }

        //写缓冲大小
        if (client.config().getWriteBufferSize() > 0) {
            real.setSendBufferSize(client.config().getWriteBufferSize());
        }

        if (client.config().getConnectTimeout() > 0) {
            real.connect(socketAddress, (int) client.config().getConnectTimeout());
        } else {
            real.connect(socketAddress);
        }

        CompletableFuture<ClientHandshakeResult> handshakeFuture = new CompletableFuture<>();
        ChannelInternal channel = new ChannelDefault<>(real, client);

        clientThread = new Thread(() -> {
            receive(channel, real, handshakeFuture);
        });
        clientThread.start();

        try {
            //开始发连接包
            channel.sendConnect(client.config().getUrl());

            //等待握手结果
            ClientHandshakeResult handshakeResult = handshakeFuture.get(client.config().getConnectTimeout(), TimeUnit.MILLISECONDS);

            if (handshakeResult.getException() != null) {
                throw handshakeResult.getException();
            } else {
                return handshakeResult.getChannel();
            }
        } catch (TimeoutException e) {
            close();
            throw new SocketdConnectionException("Connection timeout: " + client.config().getLinkUrl());
        } catch (Exception e) {
            close();

            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new SocketdConnectionException(e);
            }
        }
    }

    private void receive(ChannelInternal channel, Socket socket, CompletableFuture<ClientHandshakeResult> handshakeFuture) {
        while (!clientThread.isInterrupted()) {
            try {
                if (socket.isClosed()) {
                    client.processor().onClose(channel);
                    break;
                }

                Frame frame = client.assistant().read(socket);
                if (frame != null) {
                    client.processor().onReceive(channel, frame);

                    if (frame.getFlag() == Flags.Connack) {
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
    public void close() {
        if (real == null) {
            return;
        }

        try {
            if (real != null) {
                real.close();
            }

            if (clientThread != null) {
                clientThread.interrupt();
            }
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug("Client connector close error", e);
            }
        }
    }
}