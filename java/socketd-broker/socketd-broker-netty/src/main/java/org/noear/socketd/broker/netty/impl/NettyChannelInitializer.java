package org.noear.socketd.broker.netty.impl;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SslHandler;
import org.noear.socketd.core.Frame;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {
    SimpleChannelInboundHandler<Frame> processor;
    SSLContext sslContext;
    boolean clientMode;

    public NettyChannelInitializer(SSLContext sslContext, boolean clientMode, SimpleChannelInboundHandler<Frame> processor) {
        this.processor = processor;
        this.sslContext = sslContext;
        this.clientMode = clientMode;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslContext != null) {
            SSLEngine engine = sslContext.createSSLEngine();
            if (clientMode == false) {
                engine.setUseClientMode(false);
                engine.setNeedClientAuth(true);
            }
            pipeline.addFirst(new SslHandler(engine));
        }

        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, -4, 0));
        pipeline.addLast(new NettyMessageEncoder());
        pipeline.addLast(new NettyMessageDecoder());
        pipeline.addLast(processor);
    }
}
