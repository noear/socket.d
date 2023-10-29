package org.noear.socketd.broker.bio;

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
 * @author noear
 * @since 2.0
 */
public class BioConnector implements ClientConnector {
    private static final Logger log = LoggerFactory.getLogger(BioConnector.class);

    private BioClient client;
    private Socket socket;
    private Thread socketThread;

    public BioConnector(BioClient client) {
        this.client = client;
    }

    @Override
    public HeartbeatHandler heartbeatHandler() {
        return client.heartbeatHandler();
    }

    @Override
    public long getHeartbeatInterval() {
        return client.clientConfig.getHeartbeatInterval();
    }

    @Override
    public boolean autoReconnect() {
        return client.autoReconnect();
    }

    @Override
    public Channel connect() throws IOException {
        SocketAddress socketAddress = new InetSocketAddress(client.uri().getHost(), client.uri().getPort());


        if (client.clientConfig.getSslContext() == null) {
            socket = new Socket();
        } else {
            socket = client.clientConfig.getSslContext().getSocketFactory().createSocket();
        }

        if (client.clientConfig.getConnectTimeout() > 0) {
            socket.connect(socketAddress, (int) client.clientConfig.getConnectTimeout());
        } else {
            socket.connect(socketAddress);
        }

        CompletableFuture<Channel> future = new CompletableFuture<>();
        try {
            Channel channel = new ChannelDefault<>(socket, socket::close, client.exchanger);

            socketThread = new Thread(() -> {
                try {
                    receive(channel, socket, future);
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
            return future.get(client.clientConfig.getConnectTimeout(), TimeUnit.MILLISECONDS);
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
        if (socket == null) {
            return;
        }

        try {
            socketThread.interrupt();
            socket.close();
        } catch (Throwable e) {
            log.debug("{}", e);
        }
    }
}
