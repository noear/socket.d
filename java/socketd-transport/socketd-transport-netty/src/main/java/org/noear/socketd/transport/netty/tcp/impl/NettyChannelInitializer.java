package org.noear.socketd.transport.netty.tcp.impl;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.core.Frame;

import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;

/**
 * @author noear
 * @since 2.0
 */
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final SimpleChannelInboundHandler<Frame> processor;
    private final Config config;

    public NettyChannelInitializer(Config config, SimpleChannelInboundHandler<Frame> processor) {
        this.processor = processor;
        this.config = config;
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

        pipeline.addLast(new LengthFieldBasedFrameDecoder(Constants.MAX_SIZE_FRAME, 0, 4, -4, 0));
        pipeline.addLast(new NettyMessageEncoder(config));
        pipeline.addLast(new NettyMessageDecoder(config));
        if (config.getIdleTimeout() > 0) {
            pipeline.addLast(new IdleStateHandler(config.getIdleTimeout(), 0, 0, TimeUnit.MILLISECONDS));
            pipeline.addLast(new IdleTimeoutHandler(config));
        }
        pipeline.addLast(processor);
    }
}