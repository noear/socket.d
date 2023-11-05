package org.noear.socketd.transport.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.noear.socketd.transport.netty.tcp.impl.NettyChannelInitializer;
import org.noear.socketd.transport.netty.tcp.impl.NettyClientInboundHandler;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.exception.SocketdTimeoutException;
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
    public Channel connect() throws Exception {
        log.info("Start connecting to: {}", client.config().getUrl());

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

            return inboundHandler.getChannel().get(client.config().getConnectTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new SocketdTimeoutException("Connection timeout: " + client.config().getUrl());
        } catch (Exception e) {
            eventLoopGroup.shutdownGracefully();
            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        if (real == null) {
            return;
        }

        try {
            real.channel().close();
            eventLoopGroup.shutdownGracefully();
        } catch (Throwable e) {
            log.debug("{}", e);
        }
    }
}