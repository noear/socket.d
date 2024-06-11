package org.noear.socketd.transport.netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.noear.socketd.SocketD;
import org.noear.socketd.exception.SocketDException;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.netty.tcp.impl.NettyChannelInitializer;
import org.noear.socketd.transport.netty.tcp.impl.NettyServerInboundHandler;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.utils.NamedThreadFactory;
import org.noear.socketd.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Tcp-Nio 服务端实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class TcpNioServer extends ServerBase<TcpNioChannelAssistant> implements ChannelSupporter<Channel> {
    private static final Logger log = LoggerFactory.getLogger(TcpNioServer.class);
    private ChannelFuture server;

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workGroup;

    public TcpNioServer(ServerConfig config) {
        super(config, new TcpNioChannelAssistant());
    }

    @Override
    public String getTitle() {
        return "tcp/nio/netty 4.1/" + SocketD.version();
    }

    @Override
    public Server start() throws IOException {
        if (isStarted) {
            throw new IllegalStateException("Socket.D server started");
        } else {
            isStarted = true;
        }

        bossGroup = new NioEventLoopGroup(getConfig().getIoThreads(), new NamedThreadFactory("nettyTcpServerBoss-"));
        workGroup = new NioEventLoopGroup(getConfig().getCodecThreads(), new NamedThreadFactory("nettyTcpServerWork-"));

        try {
            NettyServerInboundHandler inboundHandler = new NettyServerInboundHandler(this);
            ChannelHandler channelHandler = new NettyChannelInitializer(getConfig(), inboundHandler);

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .childOption(ChannelOption.SO_RCVBUF, getConfig().getReadBufferSize())
                    .childOption(ChannelOption.SO_SNDBUF, getConfig().getWriteBufferSize())
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelHandler);

            if (StrUtils.isEmpty(getConfig().getHost())) {
                server = bootstrap.bind(getConfig().getPort()).await();
            } else {
                server = bootstrap.bind(getConfig().getHost(), getConfig().getPort()).await();
            }
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();

            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new SocketDException("Socket.D server start failed!", e);
            }
        }

        log.info("Socket.D server started: {server=" + getConfig().getLocalUrl() + "}");

        return this;
    }

    @Override
    public void stop() {
        if (isStarted) {
            isStarted = false;
        } else {
            return;
        }

        super.stop();

        try {
            if (server != null) {
                server.channel().close();
            }

            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }

            if (workGroup != null) {
                workGroup.shutdownGracefully();
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Server stop error", e);
            }
        }
    }
}