package org.noear.socketd.transport.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.noear.socketd.SocketD;
import org.noear.socketd.exception.SocketdException;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.netty.udp.impl.DatagramTagert;
import org.noear.socketd.transport.netty.udp.impl.NettyServerInboundHandler;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Udp-Nio 服务端实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class UdpNioServer extends ServerBase<UdpNioChannelAssistant> implements ChannelSupporter<DatagramTagert> {
    private static final Logger log = LoggerFactory.getLogger(UdpNioServer.class);
    private ChannelFuture server;
    private EventLoopGroup bossGroup;

    public UdpNioServer(ServerConfig config) {
        super(config, new UdpNioChannelAssistant(config));
    }

    @Override
    public String title() {
        return "udp/nio/netty 4.1/" + SocketD.version();
    }

    @Override
    public Server start() throws IOException {
        if (isStarted) {
            throw new IllegalStateException("Server started");
        } else {
            isStarted = true;
        }

        bossGroup = new NioEventLoopGroup(config().getCoreThreads());

        try {
            NettyServerInboundHandler inboundHandler = new NettyServerInboundHandler(this);

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup)
                    .channel(NioDatagramChannel.class)
                    //.option(ChannelOption.SO_BROADCAST,true)
                    .handler(inboundHandler);

            if (Utils.isEmpty(config().getHost())) {
                server = bootstrap.bind(config().getPort()).await();
            } else {
                server = bootstrap.bind(config().getHost(), config().getPort()).await();
            }

        } catch (Exception e) {
            bossGroup.shutdownGracefully();

            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new SocketdException(e);
            }
        }

        log.info("Socket.D server started: {server=" + config().getLocalUrl() + "}");

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
        } catch (Exception e) {
            log.debug("{}", e);
        }
    }
}