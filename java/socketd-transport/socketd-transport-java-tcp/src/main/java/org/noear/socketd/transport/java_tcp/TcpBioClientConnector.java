package org.noear.socketd.transport.java_tcp;

import org.noear.socketd.exception.SocketDConnectionException;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Flags;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.impl.ChannelDefault;
import org.noear.socketd.utils.RunUtils;
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

    private void connectDo(CompletableFuture<ClientHandshakeResult> handshakeFuture) throws IOException {
        SocketAddress socketAddress = new InetSocketAddress(client.getConfig().getHost(), client.getConfig().getPort());

        //支持 ssl
        if (client.getConfig().getSslContext() == null) {
            real = new Socket();
        } else {
            real = client.getConfig().getSslContext().getSocketFactory().createSocket();
        }

        //闲置超时
        if (client.getConfig().getIdleTimeout() > 0L) {
            //单位：毫秒
            real.setSoTimeout((int) client.getConfig().getIdleTimeout());
        }

        //读缓冲大小
        if (client.getConfig().getReadBufferSize() > 0) {
            real.setReceiveBufferSize(client.getConfig().getReadBufferSize());
        }

        //写缓冲大小
        if (client.getConfig().getWriteBufferSize() > 0) {
            real.setSendBufferSize(client.getConfig().getWriteBufferSize());
        }

        if (client.getConfig().getConnectTimeout() > 0) {
            real.connect(socketAddress, (int) client.getConfig().getConnectTimeout());
        } else {
            real.connect(socketAddress);
        }


        ChannelInternal channel = new ChannelDefault<>(real, client);

        clientThread = new Thread(() -> {
            receive(channel, real, handshakeFuture);
        });
        clientThread.start();

        //开始发连接包
        channel.sendConnect(client.getConfig().getUrl(), client.getConfig().getMetaMap());
    }

    private void receive(ChannelInternal channel, Socket socket, CompletableFuture<ClientHandshakeResult> handshakeFuture) {
        while (!clientThread.isInterrupted()) {
            try {
                if (socket.isClosed()) {
                    client.getProcessor().onClose(channel);
                    break;
                }

                Frame frame = client.getAssistant().read(socket);
                if (frame != null) {
                    if (frame.flag() == Flags.Connack) {
                        channel.onOpenFuture((r, e) -> {
                            handshakeFuture.complete(new ClientHandshakeResult(channel, e));
                        });
                    }

                    client.getProcessor().onReceive(channel, frame);
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