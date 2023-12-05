package org.noear.socketd.transport.core;

import org.noear.socketd.transport.core.buffer.BufferReader;
import org.noear.socketd.transport.core.buffer.BufferWriter;

import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;

/**
 * 配置类
 *
 * @author noear
 * @since 2.0
 */
public interface Config {
    /**
     * 是否客户端模式
     */
    boolean clientMode();

    /**
     * 获取流管理器
     * */
    StreamManger getStreamManger();

    /**
     * 获取角色名
     * */
    String getRoleName();

    /**
     * 获取字符集
     */
    Charset getCharset();

    /**
     * 获取编解码器
     */
    Codec<BufferReader, BufferWriter> getCodec();

    /**
     * 获取Id生成器
     */
    IdGenerator getIdGenerator();

    /**
     * 获取分片处理器
     */
    FragmentHandler getFragmentHandler();

    /**
     * 获取 ssl 上下文
     */
    SSLContext getSslContext();

    /**
     * 通道执行器
     * */
    ExecutorService getChannelExecutor();

    /**
     * 核心线程数（第二优先）
     */
    int getCoreThreads();

    /**
     * 最大线程数
     */
    int getMaxThreads();

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
     * */
    long getIdleTimeout();

    /**
     * 获取请求超时（单位：毫秒）
     */
    long getRequestTimeout();

    /**
     * 获取消息流超时（单位：毫秒）
     * */
    long getStreamTimeout();

    /**
     * 允许最大UDP包大小
     */
    int getMaxUdpSize();
}
