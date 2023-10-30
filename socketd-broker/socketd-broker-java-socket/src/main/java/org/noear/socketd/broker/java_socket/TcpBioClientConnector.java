package org.noear.socketd.broker.java_socket;

import org.noear.socketd.client.ClientConnectorBase;
import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.Flag;
import org.noear.socketd.protocol.Frame;
import org.noear.socketd.protocol.impl.ChannelDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
    public Channel connect() throws IOException {
        SocketAddress socketAddress = new InetSocketAddress(client.uri().getHost(), client.uri().getPort());

        //支持 ssl
        if (client.config().getSslContext() == null) {
            real = new Socket();
        } else {
            real = client.config().getSslContext().getSocketFactory().createSocket();
        }

        if (client.config().getConnectTimeout() > 0) {
            real.connect(socketAddress, (int) client.config().getConnectTimeout());
        } else {
            real.connect(socketAddress);
        }

        CompletableFuture<Channel> future = new CompletableFuture<>();

        try {
            Channel channel = new ChannelDefault<>(real, real::close, client.exchanger());

            socketThread = new Thread(() -> {
                try {
                    receive(channel, real, future);
                } catch (Throwable e) {
                    throw new IllegalStateException(e);
                }
            });

            socketThread.start();

            channel.sendConnect(client.url());
        } catch (Throwable e) {
            log.debug("{}", e);
            close();
        }

        try {
            return future.get(client.config().getConnectTimeout(), TimeUnit.MILLISECONDS);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    private void receive(Channel channel, Socket socket, CompletableFuture<Channel> future) {
        while (true) {
            try {
                if (socket.isClosed()) {
                    client.processor().onClose(channel.getSession());
                    break;
                }

                Frame frame = client.exchanger().read(socket);
                if (frame != null) {
                    client.processor().onReceive(channel, frame);

                    if (frame.getFlag() == Flag.Connack) {
                        future.complete(channel);
                    }
                }
            } catch (Throwable e) {
                client.processor().onError(channel.getSession(), e);

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