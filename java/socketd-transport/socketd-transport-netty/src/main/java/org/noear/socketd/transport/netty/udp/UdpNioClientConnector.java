package org.noear.socketd.transport.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.noear.socketd.exception.SocketdConnectionException;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.netty.udp.impl.NettyClientInboundHandler;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.exception.SocketdTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Udp-Nio 客户端连接器实现（支持 ssl）
 *
 * @author noear
 * @since 2.0
 */
public class UdpNioClientConnector extends ClientConnectorBase<UdpNioClient> {
    private static final Logger log = LoggerFactory.getLogger(UdpNioClientConnector.class);

    ChannelFuture real;

    public UdpNioClientConnector(UdpNioClient client) {
        super(client);
    }

    @Override
    public ChannelInternal connect() throws IOException {
        log.info("Start connecting to: {}", client.config().getUrl());

        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            NettyClientInboundHandler inboundHandler = new NettyClientInboundHandler(client);

            real = bootstrap.group(eventLoopGroup)
                    .channel(NioDatagramChannel.class)
                    //.option(ChannelOption.SO_BROADCAST,true)
                    .handler(inboundHandler)
                    .connect(client.config().getHost(),
                            client.config().getPort())
                    .await();

            return inboundHandler.getChannel().get(client.config().getConnectTimeout(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new SocketdTimeoutException("Connection timeout: " + client.config().getUrl());
        } catch (Exception e) {
            eventLoopGroup.shutdownGracefully();

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
            real.channel().close();
        } catch (Throwable e) {
            log.debug("{}", e);
        }
    }
}