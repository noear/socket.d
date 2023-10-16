package org.noear.socketd.broker.bio.client;

import org.noear.socketd.broker.bio.BioChannelExchanger;
import org.noear.socketd.protocol.*;
import org.noear.socketd.client.Connector;
import org.noear.socketd.protocol.impl.ChannelDefault;
import org.noear.socketd.protocol.impl.ProcessorDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author noear
 * @since 2.0
 */
public class BioConnector implements Connector {
    private static final Logger log = LoggerFactory.getLogger(BioClient.class);

    private BioClientConfig clientConfig;
    private Thread clientThread;

    private Processor processor;
    private ChannelExchanger<Socket> exchanger;

    private Listener listener;
    private String uriStr;
    private URI uri;

    public BioConnector(String uriStr, BioClientConfig clientConfig) {
        this.uriStr = uriStr;
        this.uri = URI.create(uriStr);
        this.clientConfig = clientConfig;

        this.exchanger = new BioChannelExchanger();
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public boolean autoReconnect() {
        return false;
    }

    @Override
    public Connector heartbeat(HeartbeatHandler handler) {
        return null;
    }

    @Override
    public Connector autoReconnect(boolean enable) {
        return null;
    }

    @Override
    public Connector listen(Listener listener) {
        this.listener = listener;
        this.processor = new ProcessorDefault(this.listener);
        return this;
    }

    @Override
    public Session open() throws IOException, TimeoutException {
        SocketAddress socketAddress = new InetSocketAddress(uri.getHost(), uri.getPort());
        Socket socket = new Socket();

        if (clientConfig.getSocketTimeout() > 0) {
            socket.setSoTimeout(clientConfig.getSocketTimeout());
        }

        if (clientConfig.getConnectTimeout() > 0) {
            socket.connect(socketAddress, clientConfig.getConnectTimeout());
        } else {
            socket.connect(socketAddress);
        }

        CompletableFuture<Session> future = new CompletableFuture<>();
        try {
            Channel channel = new ChannelDefault<>(socket, exchanger);

            clientThread = new Thread(() -> {
                try {
                    receive(channel, socket, future);
                } catch (Throwable e) {
                    throw new IllegalStateException(e);
                }
            });

            clientThread.start();

            channel.sendConnect(uriStr);
        } catch (Throwable e) {
            log.debug("{}", e);
            close(socket);
        }

        try {
            return future.get(clientConfig.getConnectTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    private void receive(Channel channel, Socket socket, CompletableFuture<Session> future) {
        while (true) {
            try {
                if (socket.isClosed()) {
                    processor.onClose(channel.getSession());
                    break;
                }

                Frame frame = channel.receive();
                if (frame != null) {
                    processor.onReceive(channel, frame);

                    if (frame.getFlag() == Flag.Connack) {
                        future.complete(channel.getSession());
                    }
                }
            } catch (Throwable ex) {
                processor.onError(channel.getSession(), ex);
            }
        }
    }


    private void close(Socket socket) {
        try {
            socket.close();
        } catch (Throwable e) {
            log.debug("{}", e);
        }
    }
}
