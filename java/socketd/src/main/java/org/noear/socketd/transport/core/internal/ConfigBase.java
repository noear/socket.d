package org.noear.socketd.transport.core.internal;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.codec.CodecDefault;
import org.noear.socketd.transport.core.identifier.GuidGenerator;
import org.noear.socketd.transport.core.fragment.FragmentHandlerDefault;
import org.noear.socketd.transport.core.stream.StreamMangerDefault;
import org.noear.socketd.utils.NamedThreadFactory;

import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

/**
 * 配置基类
 *
 * @author noear
 * @since 2.0
 */
public abstract class ConfigBase<T extends Config> implements Config {
    //是否客户端模式
    private final boolean clientMode;
    //流管理器
    private final StreamManger streamManger;
    //编解码器
    private final Codec codec;

    //id生成器
    private IdGenerator idGenerator;
    //分片处理
    private FragmentHandler fragmentHandler;
    //分片大小
    private int fragmentSize;

    //ssl 上下文
    private SSLContext sslContext;
    //通道执行器
    private volatile ExecutorService channelExecutor;

    //字符集
    protected Charset charset;

    //内核线程数
    protected int coreThreads;
    //最大线程数
    protected int maxThreads;

    //读缓冲大小
    protected int readBufferSize;
    //写缓冲大小
    protected int writeBufferSize;

    //连接空闲超时
    protected long idleTimeout;
    //请求默认超时
    protected long requestTimeout;
    //消息流超时（从发起到应答结束）
    protected long streamTimeout;
    //最大udp包大小
    protected int maxUdpSize;

    public ConfigBase(boolean clientMode) {
        this.clientMode = clientMode;
        this.streamManger = new StreamMangerDefault(this);
        this.codec = new CodecDefault(this);

        this.charset = StandardCharsets.UTF_8;

        this.idGenerator = new GuidGenerator();
        this.fragmentHandler = new FragmentHandlerDefault();
        this.fragmentSize = Constants.MAX_SIZE_DATA;

        this.coreThreads = Math.max(Runtime.getRuntime().availableProcessors(), 2);
        this.maxThreads = coreThreads * 4;

        this.readBufferSize = 512;
        this.writeBufferSize = 512;

        this.idleTimeout = 60_000L; //60秒（心跳默认为20秒）
        this.requestTimeout = 10_000L; //10秒（默认与连接超时同）
        this.streamTimeout = 1000 * 60 * 60 * 2;//2小时 //避免永不回调时，不能释放
        this.maxUdpSize = 2048; //2k //与 netty 保持一致 //实际可用 1464
    }

    /**
     * 是否客户端模式
     */
    @Override
    public boolean clientMode() {
        return clientMode;
    }

    @Override
    public StreamManger getStreamManger() {
        return streamManger;
    }

    /**
     * 获取角色名
     * */
    @Override
    public String getRoleName() {
        return clientMode() ? "Client" : "Server";
    }

    /**
     * 获取字符集
     */
    @Override
    public Charset getCharset() {
        return charset;
    }

    /**
     * 配置字符集
     */
    public T charset(Charset charset) {
        this.charset = charset;
        return (T) this;
    }

    /**
     * 获取编解码器
     */
    @Override
    public Codec getCodec() {
        return codec;
    }


    /**
     * 获取分片处理
     */
    @Override
    public FragmentHandler getFragmentHandler() {
        return fragmentHandler;
    }

    /**
     * 配置分片处理
     */
    public T fragmentHandler(FragmentHandler fragmentHandler) {
        Asserts.assertNull("fragmentHandler", fragmentHandler);

        this.fragmentHandler = fragmentHandler;
        return (T) this;
    }

    /**
     * 获取分片大小
     */
    @Override
    public int getFragmentSize() {
        return fragmentSize;
    }

    /**
     * 配置分片大小
     */
    public T fragmentSize(int fragmentSize) {
        if (fragmentSize > Constants.MAX_SIZE_DATA) {
            throw new IllegalArgumentException("The parameter fragmentSize cannot > 16m");
        }

        if (fragmentSize < Constants.MIN_FRAGMENT_SIZE) {
            throw new IllegalArgumentException("The parameter fragmentSize cannot < 1k");
        }

        this.fragmentSize = fragmentSize;
        return (T) this;
    }

    /**
     * 获取标识生成器
     */
    @Override
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    /**
     * 配置标识生成器
     */
    public T idGenerator(IdGenerator idGenerator) {
        Asserts.assertNull("idGenerator", idGenerator);

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

    private Object EXECUTOR_LOCK = new Object();

    @Override
    public ExecutorService getChannelExecutor() {
        if (channelExecutor == null) {
            synchronized (EXECUTOR_LOCK) {
                if (channelExecutor == null) {
                    int nThreads = clientMode() ? coreThreads : maxThreads;

                    channelExecutor = new ThreadPoolExecutor(nThreads, nThreads,
                            0L, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<Runnable>(),
                            new NamedThreadFactory("Socketd-channelExecutor-"));
                }
            }
        }

        return channelExecutor;
    }

    /**
     * 配置调试执行器
     * */
    public T channelExecutor(ExecutorService channelExecutor) {
        ExecutorService odl = this.channelExecutor;
        this.channelExecutor = channelExecutor;

        if (odl != null) {
            odl.shutdown();
        }

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
        this.maxThreads = coreThreads * 4;
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


    /**
     * 获取读缓冲大小
     */
    public int getReadBufferSize() {
        return readBufferSize;
    }

    /**
     * 配置读缓冲大小
     */
    public T readBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
        return (T) this;
    }

    /**
     * 获取写缓冲大小
     */
    public int getWriteBufferSize() {
        return writeBufferSize;
    }

    /**
     * 配置写缓冲大小
     */
    public T writeBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
        return (T) this;
    }


    /**
     * 获取连接空闲超时
     */
    public long getIdleTimeout() {
        return idleTimeout;
    }

    /**
     * 配置连接空闲超时
     */
    public T idleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
        return (T) this;
    }

    /**
     * 获取请求默认超时
     */
    @Override
    public long getRequestTimeout() {
        return requestTimeout;
    }

    /**
     * 配置请求默认超时
     */
    public T requestTimeout(long requestTimeout) {
        this.requestTimeout = requestTimeout;
        return (T) this;
    }

    /**
     * 获取消息流超时（单位：毫秒）
     * */
    @Override
    public long getStreamTimeout() {
        return streamTimeout;
    }

    /**
     * 配置消息流超时（单位：毫秒）
     * */
    public T streamTimeout(long streamTimeout) {
        this.streamTimeout = streamTimeout;
        return (T) this;
    }

    /**
     * 获取允许最大UDP包大小
     */
    @Override
    public int getMaxUdpSize() {
        return maxUdpSize;
    }

    /**
     * 配置允许最大UDP包大小
     */
    public T maxUdpSize(int maxUdpSize) {
        this.maxUdpSize = maxUdpSize;
        return (T) this;
    }
}