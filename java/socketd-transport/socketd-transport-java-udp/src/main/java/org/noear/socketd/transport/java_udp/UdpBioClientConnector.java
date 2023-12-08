package org.noear.socketd.transport.java_udp;

import org.noear.socketd.exception.SocketdConnectionException;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.java_udp.impl.DatagramFrame;
import org.noear.socketd.transport.java_udp.impl.DatagramTagert;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.transport.core.Flags;
import org.noear.socketd.transport.core.internal.ChannelDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

/**
 * Udp 客户端连接器实现（支持 ssl）
 *
 * @author Urara
 * @since 2.0
 */
public class UdpBioClientConnector extends ClientConnectorBase<UdpBioClient> {
    private static final Logger log = LoggerFactory.getLogger(UdpBioClientConnector.class);

    private DatagramSocket real;
    private Thread clientThread;

    public UdpBioClientConnector(UdpBioClient client) {
        super(client);
    }

    @Override
    public ChannelInternal connect() throws IOException {
        //不要复用旧的对象

        real = new DatagramSocket();

        SocketAddress socketAddress = new InetSocketAddress(client.config().getHost(), client.config().getPort());
        real.connect(socketAddress);

        DatagramTagert tagert = new DatagramTagert(real, null, true);
        ChannelInternal channel = new ChannelDefault<>(tagert, client);

        CompletableFuture<ClientHandshakeResult> handshakeFuture = new CompletableFuture<>();

        //定义接收线程
        clientThread = new Thread(() -> {
            try {
                receive(channel, real, handshakeFuture);
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
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

    private void receive(ChannelInternal channel, DatagramSocket socket, CompletableFuture<ClientHandshakeResult> handshakeFuture) {
        while (!clientThread.isInterrupted()) {
            try {
                if (socket.isClosed()) {
                    client.processor().onClose(channel);
                    break;
                }

                DatagramFrame frame = client.assistant().read(socket);
                if (frame != null) {
                    client.processor().onReceive(channel, frame.getFrame());

                    if (frame.getFrame().getFlag() == Flags.Connack) {
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