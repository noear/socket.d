package org.noear.socketd.broker.netty.impl;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SslHandler;
import org.noear.socketd.core.Config;
import org.noear.socketd.core.Frame;

import javax.net.ssl.SSLEngine;

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

        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, -4, 0));
        pipeline.addLast(new NettyMessageEncoder(config));
        pipeline.addLast(new NettyMessageDecoder(config));
        pipeline.addLast(processor);
    }
}
