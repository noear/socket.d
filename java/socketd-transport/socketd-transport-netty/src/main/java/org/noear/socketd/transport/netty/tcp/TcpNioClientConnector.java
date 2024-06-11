package org.noear.socketd.transport.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.noear.socketd.exception.SocketDConnectionException;
import org.noear.socketd.transport.client.ClientHandshakeResult;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.netty.tcp.impl.NettyChannelInitializer;
import org.noear.socketd.transport.netty.tcp.impl.NettyClientInboundHandler;
import org.noear.socketd.transport.client.ClientConnectorBase;
import org.noear.socketd.utils.NamedThreadFactory;
import org.noear.socketd.utils.RunUtils;
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
    private NioEventLoopGroup workGroup;

    public TcpNioClientConnector(TcpNioClient client) {
        super(client);
    }

    @Override
    public ChannelInternal connect() throws IOException {
        //关闭之前的资源
        close();

        workGroup = new NioEventLoopGroup(getConfig().getCodecThreads(), new NamedThreadFactory("nettyTcpClientWork-"));

        //workGroup.setIoRatio(getConfig().getIoRatio());

        try {
            Bootstrap bootstrap = new Bootstrap();

            NettyClientInboundHandler inboundHandler = new NettyClientInboundHandler(client);
            ChannelHandler handler = new NettyChannelInitializer(client.getConfig(), inboundHandler);

            real = bootstrap.group(workGroup)
                    .option(ChannelOption.SO_RCVBUF, getConfig().getReadBufferSize())
                    .option(ChannelOption.SO_SNDBUF, getConfig().getWriteBufferSize())
                    .channel(NioSocketChannel.class)
                    .handler(handler)
                    .connect(client.getConfig().getHost(),
                            client.getConfig().getPort())
                    .await();

            //等待握手结果
            ClientHandshakeResult handshakeResult = inboundHandler.getHandshakeFuture().get(client.getConfig().getConnectTimeout(), TimeUnit.MILLISECONDS);

            if (handshakeResult.getThrowable() != null) {
                throw handshakeResult.getThrowable();
            } else {
                return handshakeResult.getChannel();
            }
        } catch (TimeoutException e) {
            close();
            throw new SocketDConnectionException("Connection timeout: " + client.getConfig().getLinkUrl());
        } catch (Throwable e) {
            close();

            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new SocketDConnectionException("Connection failed: " + client.getConfig().getLinkUrl(), e);
            }
        }
    }

    @Override
    public void close() {
        try {
            if (real != null) {
                real.channel().close();
            }

            if (workGroup != null) {
                workGroup.shutdownGracefully();
            }
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug("Client connector close error", e);
            }
        }
    }
}