package org.noear.socketd.broker.netty.impl;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SslHandler;
import org.noear.socketd.core.Codec;
import org.noear.socketd.core.Frame;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.0
 */
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {
    private SimpleChannelInboundHandler<Frame> processor;
    private Codec<ByteBuffer> codec;
    private SSLContext sslContext;
    private boolean clientMode;

    public NettyChannelInitializer(Codec<ByteBuffer> codec, SSLContext sslContext, boolean clientMode, SimpleChannelInboundHandler<Frame> processor) {
        this.processor = processor;
        this.codec = codec;
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
        pipeline.addLast(new NettyMessageEncoder(codec));
        pipeline.addLast(new NettyMessageDecoder(codec));
        pipeline.addLast(processor);
    }
}
