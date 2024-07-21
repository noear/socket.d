package org.noear.socketd.transport.core;

import org.noear.socketd.transport.stream.StreamManger;

import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;

/**
 * 配置接口
 *
 * @author noear
 * @since 2.0
 */
public interface Config {
    /**
     * 是否客户模式
     */
    boolean clientMode();

    /**
     * 是否串行发送
     */
    boolean isSerialSend();

    /**
     * 是否无锁发送
     *
     * @deprecated 2.5
     */
    @Deprecated
    boolean isNolockSend();

    /**
     * 获取流管理器
     */
    StreamManger getStreamManger();

    /**
     * 获取角色名
     */
    String getRoleName();

    /**
     * 获取字符集
     */
    Charset getCharset();

    /**
     * 获取编解码器
     */
    Codec getCodec();

    /**
     * 生成Id
     */
    String genId();

    /**
     * 获取分片处理器
     */
    FragmentHandler getFragmentHandler();

    /**
     * 获取分片大小
     */
    int getFragmentSize();

    /**
     * 获取 ssl 上下文
     */
    SSLContext getSslContext();

    /**
     * Io线程数
     */
    int getIoThreads();

    /**
     * 解码线程数
     */
    int getCodecThreads();

    /**
     * 工作线程数
     */
    int getWorkThreads();

    /**
     * 工作执行器
     */
    ExecutorService getWorkExecutor();

    /**
     * 交换线程数
     *
     * @deprecated 2.4
     */
    @Deprecated
    default int getExchangeThreads() {
        return getWorkThreads();
    }

    /**
     * 交换执行器
     *
     * @deprecated 2.4
     */
    @Deprecated
    default ExecutorService getExchangeExecutor() {
        return getWorkExecutor();
    }

    /**
     * 获取读缓冲大小
     */
    int getReadBufferSize();

    /**
     * 配置读缓冲大小
     */
    int getWriteBufferSize();

    /**
     * 获取连接空闲超时（单位：毫秒）
     */
    long getIdleTimeout();

    /**
     * 获取请求超时（单位：毫秒）
     */
    long getRequestTimeout();

    /**
     * 获取消息流超时（单位：毫秒）
     */
    long getStreamTimeout();

    /**
     * 允许最大UDP包大小
     */
    int getMaxUdpSize();

    /**
     * 使用最大内存限制
     */
    boolean useMaxMemoryLimit();

    /**
     * 允许最大内存使用比例（0.x->1.0）
     */
    float getMaxMemoryRatio();

    /**
     * 流量限制器
     */
    TrafficLimiter getTrafficLimiter();

    /**
     * 是否使用子协议
     */
    boolean isUseSubprotocols();
}