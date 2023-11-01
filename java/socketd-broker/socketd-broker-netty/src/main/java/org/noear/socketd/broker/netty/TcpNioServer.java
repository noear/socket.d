package org.noear.socketd.broker.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.noear.socketd.broker.netty.impl.NettyChannelInitializer;
import org.noear.socketd.broker.netty.impl.NettyServerInboundHandler;
import org.noear.socketd.server.ServerBase;
import org.noear.socketd.server.ServerConfig;
import org.noear.socketd.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;

/**
 * Tcp-Nio 服务端实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class TcpNioServer extends ServerBase<TcpNioChannelAssistant> {
    private static final Logger log = LoggerFactory.getLogger(TcpNioServer.class);
    private ChannelFuture server;

    public TcpNioServer(ServerConfig config) {
        super(config, new TcpNioChannelAssistant());
    }

    @Override
    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(config().getCoreThreads());
        EventLoopGroup workGroup = new NioEventLoopGroup(config().getMaxThreads());


        try {
            NettyServerInboundHandler inboundHandler = new NettyServerInboundHandler(this);
            SSLContext sslContext = config().getSslContext();
            ChannelHandler handler = new NettyChannelInitializer(config().getCodec(), sslContext, false, inboundHandler);

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(handler);

            if (Utils.isEmpty(config().getHost())) {
                server = bootstrap.bind(config().getPort()).await();
            } else {
                server = bootstrap.bind(config().getHost(), config().getPort()).await();
            }

            log.info("Server started: {server=tcp://127.0.0.1:" + config().getPort() + "}");
        } catch (RuntimeException e) {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();

            throw e;
        } catch (Throwable e) {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();

            throw new IllegalStateException(e);
        }
    }

    @Override
    public void stop() throws Exception {
        if (server == null) {
            return;
        }

        server.channel().close();
    }
}