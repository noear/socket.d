package org.noear.socketd.broker.java_udp;

import org.noear.socketd.broker.java_udp.impl.DatagramFrame;
import org.noear.socketd.broker.java_udp.impl.DatagramTagert;
import org.noear.socketd.client.ClientConnectorBase;
import org.noear.socketd.exception.SocketdTimeoutException;
import org.noear.socketd.core.Channel;
import org.noear.socketd.core.Flag;
import org.noear.socketd.core.impl.ChannelDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Udp 客户端连接器实现（支持 ssl）
 *
 * @author Urara
 * @since 2.0
 */
public class UdpClientConnector extends ClientConnectorBase<UdpClient> {
    private static final Logger log = LoggerFactory.getLogger(UdpClientConnector.class);

    private DatagramSocket real;
    private Thread receiveThread;

    public UdpClientConnector(UdpClient client) {
        super(client);
    }

    @Override
    public Channel connect() throws Exception {
        real = new DatagramSocket();

        SocketAddress socketAddress = new InetSocketAddress(client.config().getUri().getHost(), client.config().getUri().getPort());
        real.connect(socketAddress);

        DatagramTagert tagert = new DatagramTagert(real, null, true);
        Channel channel = new ChannelDefault<>(tagert, client.config().getMaxRequests(), client.assistant());

        CompletableFuture<Channel> channelFuture = new CompletableFuture<>();
        //定义接收线程
        receiveThread = new Thread(() -> {
            try {
                receive(channel, real, channelFuture);
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        });

        receiveThread.start();

        //开始发连接包
        channel.sendConnect(client.config().getUrl());

        try {
            return channelFuture.get(client.config().getConnectTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new SocketdTimeoutException("Connection timeout: " + client.config().getUrl());
        } catch (Exception e) {
            throw e;
        }
    }

    private void receive(Channel channel, DatagramSocket socket, CompletableFuture<Channel> channelFuture) {
        while (true) {
            try {
                if (socket.isClosed()) {
                    client.processor().onClose(channel.getSession());
                    break;
                }

                DatagramFrame frame = client.assistant().read(socket);
                if (frame != null) {
                    client.processor().onReceive(channel, frame.getFrame());

                    if(frame.getFrame().getFlag() == Flag.Connack){
                        channelFuture.complete(channel);
                    }
                }

            }  catch (Throwable e) {
                client.processor().onError(channel.getSession(), e);

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
            receiveThread.interrupt();
            real.close();
        } catch (Throwable e) {
            log.debug("{}", e);
        }
    }
}