package org.noear.socketd.transport.core;

import org.noear.socketd.transport.core.impl.IdGeneratorGuid;
import org.noear.socketd.transport.core.impl.RangesHandlerDefault;

import javax.net.ssl.SSLContext;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author noear
 * @since 2.0
 */
public abstract class ConfigBase<T extends Config> implements Config {
    private final boolean clientMode;

    protected Charset charset;

    protected Codec<ByteBuffer> codec;
    protected IdGenerator idGenerator;
    protected RangesHandler rangesHandler;
    protected SSLContext sslContext;

    protected int maxRequests;
    protected int maxUdpSize;
    protected int rangeSize;

    public ConfigBase(boolean clientMode) {
        this.clientMode = clientMode;

        this.charset = StandardCharsets.UTF_8;

        this.codec = new CodecByteBuffer(this);
        this.idGenerator = new IdGeneratorGuid();
        this.rangesHandler = new RangesHandlerDefault();

        this.maxRequests = 10;
        this.maxUdpSize = 2048; //2k //与 netty 保持一致 //实际可用 1464
        this.rangeSize = 1024 * 1024 * 16; //16m
    }

    @Override
    public boolean clientMode() {
        return clientMode;
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    public T charset(Charset charset) {
        this.charset = charset;
        return (T) this;
    }

    /**
     * 获取编解码器
     */
    @Override
    public Codec<ByteBuffer> getCodec() {
        return codec;
    }

    public T codec(Codec<ByteBuffer> codec) {
        this.codec = codec;
        return (T) this;
    }


    @Override
    public RangesHandler getRangesHandler() {
        return rangesHandler;
    }

    public T rangesHandler(RangesHandler rangesHandler) {
        this.rangesHandler = rangesHandler;
        return (T) this;
    }

    /**
     * 获取标识生成器
     */
    @Override
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }


    public T idGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        return (T) this;
    }


    /**
     * 获取 ssl 上下文
     */
    @Override
    public SSLContext getSslContext() {
        return sslContext;
    }

    /**
     * 配置 ssl 上下文
     */
    public T sslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return (T) this;
    }


    /**
     * 允许最大请求数
     */
    @Override
    public int getMaxRequests() {
        return maxRequests;
    }

    public T maxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
        return (T) this;
    }

    /**
     * 允许最大UDP包大小
     */
    @Override
    public int getMaxUdpSize() {
        return maxUdpSize;
    }

    public T maxUdpSize(int maxUdpSize) {
        this.maxUdpSize = maxUdpSize;
        return (T) this;
    }

    /**
     * 获取分片大小
     */
    @Override
    public int getRangeSize() {
        return rangeSize;
    }

    public T rangeSize(int rangeSize) {
        this.rangeSize = rangeSize;
        return (T) this;
    }
}