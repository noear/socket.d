package org.noear.socketd.transport.java_udp;

import org.noear.socketd.exception.SocketDConnectionException;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.java_udp.impl.DatagramFrame;
import org.noear.socketd.transport.java_udp.impl.DatagramTagert;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.transport.core.Flags;
import org.noear.socketd.transport.core.impl.ChannelDefault;
import org.noear.socketd.utils.RunUtils;
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
        //关闭之前的资源
        close();

        CompletableFuture<ClientHandshakeResult> handshakeFuture = new CompletableFuture<>();

        RunUtils.async(() -> {
            try {
                connectDo(handshakeFuture);
            } catch (Throwable e) {
                handshakeFuture.complete(new ClientHandshakeResult(null, e));
            }
        });

        try {
            //等待握手结果
            ClientHandshakeResult handshakeResult = handshakeFuture.get(client.getConfig().getConnectTimeout(), TimeUnit.MILLISECONDS);

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

    private void connectDo(CompletableFuture<ClientHandshakeResult> handshakeFuture) throws IOException{
        //不要复用旧的对象
        real = new DatagramSocket();

        SocketAddress socketAddress = new InetSocketAddress(client.getConfig().getHost(), client.getConfig().getPort());
        real.connect(socketAddress);

        DatagramTagert tagert = new DatagramTagert(real, null, true);
        ChannelInternal channel = new ChannelDefault<>(tagert, client);



        //定义接收线程
        clientThread = new Thread(() -> {
            try {
                receive(channel, real, handshakeFuture);
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        });
        clientThread.start();

        //开始发连接包
        channel.sendConnect(client.getConfig().getUrl(), client.getConfig().getMetaMap());
    }

    private void receive(ChannelInternal channel, DatagramSocket socket, CompletableFuture<ClientHandshakeResult> handshakeFuture) {
        while (!clientThread.isInterrupted()) {
            try {
                if (socket.isClosed()) {
                    client.getProcessor().onClose(channel);
                    break;
                }

                DatagramFrame frame = client.getAssistant().read(socket);
                if (frame != null) {
                    if (frame.getFrame().flag() == Flags.Connack) {
                        channel.onOpenFuture((r, e) -> {
                            handshakeFuture.complete(new ClientHandshakeResult(channel, e));
                        });
                    }

                    client.getProcessor().onReceive(channel, frame.getFrame());
                }

            } catch (Exception e) {
                if (e instanceof SocketDConnectionException) {
                    //说明握手失败了
                    handshakeFuture.complete(new ClientHandshakeResult(channel, e));
                    break;
                }

                client.getProcessor().onError(channel, e);

                if (e instanceof SocketException) {
                    break;
                }
            }
        }
    }

    @Override
    public void close() {
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