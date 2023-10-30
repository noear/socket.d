package org.noear.socketd.broker.bio;

import org.noear.socketd.client.ClientConfig;
import org.noear.socketd.client.ClientConnector;
import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.Flag;
import org.noear.socketd.protocol.Frame;
import org.noear.socketd.protocol.HeartbeatHandler;
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
 * Bio 客户端连接器实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpBioClientConnector implements ClientConnector {
    private static final Logger log = LoggerFactory.getLogger(TcpBioClientConnector.class);

    private final TcpBioClient client;
    private final ClientConfig clientConfig;

    private Socket real;
    private Thread socketThread;

    public TcpBioClientConnector(TcpBioClient client) {
        this.client = client;
        this.clientConfig = client.clientConfig();
    }

    @Override
    public HeartbeatHandler heartbeatHandler() {
        return client.heartbeatHandler();
    }

    @Override
    public long getHeartbeatInterval() {
        return clientConfig.getHeartbeatInterval();
    }

    @Override
    public boolean autoReconnect() {
        return client.autoReconnect();
    }

    @Override
    public Channel connect() throws IOException {
        SocketAddress socketAddress = new InetSocketAddress(client.uri().getHost(), client.uri().getPort());


        if (clientConfig.getSslContext() == null) {
            real = new Socket();
        } else {
            real = clientConfig.getSslContext().getSocketFactory().createSocket();
        }

        if (clientConfig.getConnectTimeout() > 0) {
            real.connect(socketAddress, (int) clientConfig.getConnectTimeout());
        } else {
            real.connect(socketAddress);
        }

        CompletableFuture<Channel> future = new CompletableFuture<>();
        try {
            Channel channel = new ChannelDefault<>(real, real::close, client.exchanger);

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
            return future.get(clientConfig.getConnectTimeout(), TimeUnit.MILLISECONDS);
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

                Frame frame = client.exchanger.read(socket);
                if (frame != null) {
                    client.processor().onReceive(channel, frame);

                    if (frame.getFlag() == Flag.Connack) {
                        future.complete(channel);
                    }
                }
            } catch (Throwable e) {
                client.processor().onError(channel.getSession(), e);

                if(e instanceof SocketException){
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
