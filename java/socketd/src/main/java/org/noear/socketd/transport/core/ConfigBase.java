package org.noear.socketd.transport.core;

import org.noear.socketd.transport.core.impl.IdGeneratorGuid;
import org.noear.socketd.transport.core.impl.FragmentHandlerDefault;

import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

/**
 * @author noear
 * @since 2.0
 */
public abstract class ConfigBase<T extends Config> implements Config {
    private final boolean clientMode;

    protected Charset charset;

    protected Codec<BufferReader, BufferWriter> codec;
    protected IdGenerator idGenerator;
    protected FragmentHandler fragmentHandler;

    protected SSLContext sslContext;
    protected ExecutorService executor;

    protected int coreThreads;
    protected int maxThreads;

    protected long peplyTimeout;
    protected int maxRequests;
    protected int maxUdpSize;

    public ConfigBase(boolean clientMode) {
        this.clientMode = clientMode;

        this.charset = StandardCharsets.UTF_8;

        this.codec = new CodecByteBuffer(this);
        this.idGenerator = new IdGeneratorGuid();
        this.fragmentHandler = new FragmentHandlerDefault();

        this.coreThreads = Runtime.getRuntime().availableProcessors() * 2;
        this.maxThreads = coreThreads * 8;

        this.peplyTimeout = 3000;
        this.maxRequests = 10;
        this.maxUdpSize = 2048; //2k //与 netty 保持一致 //实际可用 1464
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
    public Codec<BufferReader, BufferWriter> getCodec() {
        return codec;
    }

    public T codec(Codec<BufferReader, BufferWriter> codec) {
        this.codec = codec;
        return (T) this;
    }


    @Override
    public FragmentHandler getFragmentHandler() {
        return fragmentHandler;
    }

    public T fragmentHandler(FragmentHandler fragmentHandler) {
        this.fragmentHandler = fragmentHandler;
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
     * 获取执行器
     * */
    public ExecutorService getExecutor() {
        return executor;
    }

    public T executor(ExecutorService executor) {
        this.executor = executor;
        return (T) this;
    }

    /**
     * 获取核心线程数
     */
    @Override
    public int getCoreThreads() {
        return coreThreads;
    }

    /**
     * 配置核心线程数
     */
    public T coreThreads(int coreThreads) {
        this.coreThreads = coreThreads;
        return (T) this;
    }

    /**
     * 获取最大线程数
     */
    @Override
    public int getMaxThreads() {
        return maxThreads;
    }

    /**
     * 配置最大线程数
     */
    public T maxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        return (T) this;
    }

    @Override
    public long getReplyTimeout() {
        return peplyTimeout;
    }

    public T peplyTimeout(long peplyTimeout) {
        this.peplyTimeout = peplyTimeout;
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
}