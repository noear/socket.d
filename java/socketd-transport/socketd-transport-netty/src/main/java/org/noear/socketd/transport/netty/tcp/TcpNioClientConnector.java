package org.noear.socketd.transport.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.noear.socketd.exception.SocketdConnectionException;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.netty.tcp.impl.NettyChannelInitializer;
import org.noear.socketd.transport.netty.tcp.impl.NettyClientInboundHandler;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.transport.core.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tcp-Nio 客户端连接器实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class TcpNioClientConnector extends ClientConnectorBase<TcpNioClient> {
    private static final Logger log = LoggerFactory.getLogger(TcpNioClientConnector.class);

    private ChannelFuture real;
    private NioEventLoopGroup eventLoopGroup;

    public TcpNioClientConnector(TcpNioClient client) {
        super(client);
    }

    @Override
    public ChannelInternal connect() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Client connector start connecting to: {}", client.config().getUrl());
        }

        eventLoopGroup = new NioEventLoopGroup(client.config().getCoreThreads());

        try {
            Bootstrap bootstrap = new Bootstrap();

            NettyClientInboundHandler inboundHandler = new NettyClientInboundHandler(client);
            ChannelHandler handler = new NettyChannelInitializer(client.config(), inboundHandler);

            real = bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(handler)
                    .connect(client.config().getHost(),
                            client.config().getPort())
                    .await();

            //等待握手结果
            ClientHandshakeResult handshakeResult = inboundHandler.getHandshakeFuture().get(client.config().getConnectTimeout(), TimeUnit.MILLISECONDS);

            if (handshakeResult.getException() != null) {
                throw handshakeResult.getException();
            } else {
                return handshakeResult.getChannel();
            }
        } catch (TimeoutException e) {
            close();
            throw new SocketdConnectionException("Connection timeout: " + client.config().getUrl());
        } catch (Exception e) {
            close();

            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new SocketdConnectionException(e);
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
                real.channel().close();
            }

            if (eventLoopGroup != null) {
                eventLoopGroup.shutdownGracefully();
            }
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug("Client connector close error", e);
            }
        }
    }
}