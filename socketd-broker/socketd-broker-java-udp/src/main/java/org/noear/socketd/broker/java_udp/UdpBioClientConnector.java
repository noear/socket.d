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
public class UdpBioClientConnector extends ClientConnectorBase<UdpBioClient> {
    private static final Logger log = LoggerFactory.getLogger(UdpBioClientConnector.class);

    private DatagramSocket socket;
    private Thread receiveThread;

    public UdpBioClientConnector(UdpBioClient client) {
        super(client);
    }

    @Override
    public Channel connect() throws Exception {
        socket = new DatagramSocket();

        SocketAddress socketAddress = new InetSocketAddress(client.config().getUri().getHost(), client.config().getUri().getPort());
        socket.connect(socketAddress);

        DatagramTagert tagert = new DatagramTagert(socket, null, true);
        Channel channel = new ChannelDefault<>(tagert, client.config().getMaxRequests(), client.assistant());

        CompletableFuture<Channel> channelFuture = new CompletableFuture<>();
        //定义接收线程
        receiveThread = new Thread(() -> {
            while (true) {
                try {
                    DatagramFrame datagramFrame = client.assistant().read(socket);
                    if (datagramFrame == null) {
                        continue;
                    }

                    client.processor().onReceive(channel, datagramFrame.getFrame());

                    if(datagramFrame.getFrame().getFlag() == Flag.Connack){
                        channelFuture.complete(channel);
                    }
                } catch (Throwable e) {
                    log.warn("{}", e);
                }
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

    @Override
    public void close() {
        if (socket == null) {
            return;
        }

        try {
            receiveThread.interrupt();
            socket.close();
        } catch (Throwable e) {
            log.debug("{}", e);
        }
    }
}