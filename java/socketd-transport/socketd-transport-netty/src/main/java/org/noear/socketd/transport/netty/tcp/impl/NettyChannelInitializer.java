package org.noear.socketd.transport.netty.tcp.impl;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.utils.NamedThreadFactory;

import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;

/**
 * @author noear
 * @since 2.0
 */
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final SimpleChannelInboundHandler<Frame> processor;
    private final Config config;
    private final DefaultEventExecutorGroup defaultEventExecutorGroup;

    public NettyChannelInitializer(Config config, SimpleChannelInboundHandler<Frame> processor, DefaultEventExecutorGroup defaultEventExecutorGroup) {
        this.processor = processor;
        this.config = config;
        this.defaultEventExecutorGroup = defaultEventExecutorGroup;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (config.getSslContext() != null) {
            SSLEngine engine = config.getSslContext().createSSLEngine();
            if (config.clientMode() == false) {
                engine.setUseClientMode(false);
                engine.setNeedClientAuth(true);
            }
            pipeline.addFirst(new SslHandler(engine));
        }

        pipeline.addLast(defaultEventExecutorGroup,
                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, -4, 0),
                new NettyMessageEncoder(config),
                new NettyMessageDecoder(config));
        if (config.getIdleTimeout() > 0) {
            pipeline.addLast(defaultEventExecutorGroup,
                    new IdleStateHandler(config.getIdleTimeout(), 0, 0, TimeUnit.MILLISECONDS),
                    new IdleTimeoutHandler(config));
        }
        pipeline.addLast(defaultEventExecutorGroup, processor);
    }
}
