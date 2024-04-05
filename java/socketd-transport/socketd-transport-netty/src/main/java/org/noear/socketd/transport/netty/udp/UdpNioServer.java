package org.noear.socketd.transport.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.noear.socketd.SocketD;
import org.noear.socketd.exception.SocketDException;
import org.noear.socketd.transport.core.ChannelSupporter;
import org.noear.socketd.transport.netty.udp.impl.DatagramTagert;
import org.noear.socketd.transport.netty.udp.impl.NettyServerInboundHandler;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.noear.socketd.utils.NamedThreadFactory;
import org.noear.socketd.utils.StrUtils;
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
    public String getTitle() {
        return "udp/nio/netty 4.1/" + SocketD.version();
    }

    @Override
    public Server start() throws IOException {
        if (isStarted) {
            throw new IllegalStateException("Socket.D server started");
        } else {
            isStarted = true;
        }

        bossGroup = new NioEventLoopGroup(getConfig().getCodecThreads(), new NamedThreadFactory("nettyUdpServerBoss-"));

        try {
            NettyServerInboundHandler inboundHandler = new NettyServerInboundHandler(this);

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup)
                    .channel(NioDatagramChannel.class)
                    //.option(ChannelOption.SO_BROADCAST,true)
                    .handler(inboundHandler);

            if (StrUtils.isEmpty(getConfig().getHost())) {
                server = bootstrap.bind(getConfig().getPort()).await();
            } else {
                server = bootstrap.bind(getConfig().getHost(), getConfig().getPort()).await();
            }

        } catch (Exception e) {
            bossGroup.shutdownGracefully();

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
        } catch (Exception e) {
            log.debug("{}", e);
        }
    }
}