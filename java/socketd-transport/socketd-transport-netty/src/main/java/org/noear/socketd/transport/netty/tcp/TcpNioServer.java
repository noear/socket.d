package org.noear.socketd.transport.netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.noear.socketd.transport.netty.tcp.impl.NettyChannelInitializer;
import org.noear.socketd.transport.netty.tcp.impl.NettyServerInboundHandler;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tcp-Nio 服务端实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class TcpNioServer extends ServerBase<TcpNioChannelAssistant> {
    private static final Logger log = LoggerFactory.getLogger(TcpNioServer.class);
    private ChannelFuture server;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;

    public TcpNioServer(ServerConfig config) {
        super(config, new TcpNioChannelAssistant());
    }

    @Override
    public Server start() throws Exception {
        if (isStarted) {
            throw new IllegalStateException("Server started");
        } else {
            isStarted = true;
        }

        bossGroup = new NioEventLoopGroup(config().getCoreThreads());
        workGroup = new NioEventLoopGroup(config().getMaxThreads());

        try {
            NettyServerInboundHandler inboundHandler = new NettyServerInboundHandler(this);
            ChannelHandler channelHandler = new NettyChannelInitializer(config(), inboundHandler);

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelHandler);

            if (Utils.isEmpty(config().getHost())) {
                server = bootstrap.bind(config().getPort()).await();
            } else {
                server = bootstrap.bind(config().getHost(), config().getPort()).await();
            }

            log.info("Server started: {server=" + config().getLocalUrl() + "}");
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();

            throw e;
        }

        return this;
    }

    @Override
    public void stop() {
        if (isStarted) {
            isStarted = false;
        } else {
            return;
        }

        try {
            server.channel().close();
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        } catch (Exception e) {
            log.debug("{}", e);
        }
    }
}