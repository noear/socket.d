package org.noear.socketd.broker.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.noear.socketd.broker.netty.impl.NettyChannelInitializer;
import org.noear.socketd.broker.netty.impl.NettyClientProcessor;
import org.noear.socketd.client.ClientConnectorBase;
import org.noear.socketd.protocol.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
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
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            NettyClientProcessor processor = new NettyClientProcessor(client);
            SSLContext sslContext = client.config().getSslContext();
            ChannelHandler handler = new NettyChannelInitializer(sslContext, true, processor);

            real = bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(handler)
                    .connect(client.config().getUri().getHost(),
                            client.config().getUri().getPort())
                    .sync();

            return processor.getChannel().get(client.config().getConnectTimeout(), TimeUnit.MILLISECONDS);
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
