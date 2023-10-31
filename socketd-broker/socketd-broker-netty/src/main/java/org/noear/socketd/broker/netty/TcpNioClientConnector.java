package org.noear.socketd.broker.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.noear.socketd.broker.netty.impl.NettyChannelInitializer;
import org.noear.socketd.broker.netty.impl.NettyClientInboundHandler;
import org.noear.socketd.client.ClientConnectorBase;
import org.noear.socketd.exception.SocktedTimeoutException;
import org.noear.socketd.protocol.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
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

    ChannelFuture real;

    public TcpNioClientConnector(TcpNioClient client) {
        super(client);
    }

    @Override
    public Channel connect() throws Exception {
        log.info("Start connecting to: {}", client.config().getUrl());

        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            NettyClientInboundHandler inboundHandler = new NettyClientInboundHandler(client);
            SSLContext sslContext = client.config().getSslContext();
            ChannelHandler handler = new NettyChannelInitializer(sslContext, true, inboundHandler);

            real = bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(handler)
                    .connect(client.config().getUri().getHost(),
                            client.config().getUri().getPort())
                    .await();

            return inboundHandler.getChannel().get(client.config().getConnectTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new SocktedTimeoutException("Connection timeout: " + client.config().getUrl());
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
        } catch (Throwable e) {
            log.debug("{}", e);
        }
    }
}
