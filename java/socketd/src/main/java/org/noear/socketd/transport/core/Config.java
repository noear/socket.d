package org.noear.socketd.transport.core;

import javax.net.ssl.SSLContext;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

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
     * 获取协议架构
     */
    String getSchema();

    /**
     * 获取字符集
     */
    Charset getCharset();

    /**
     * 获取编解码器
     */
    Codec<ByteBuffer> getCodec();

    /**
     * 获取Id生成器
     */
    IdGenerator getIdGenerator();

    /**
     * 获取分布处理器
     * */
    RangesHandler getRangesHandler();

    /**
     * 获取 ssl 上下文
     */
    SSLContext getSslContext();

    /**
     * 允许最大请求数
     */
    int getMaxRequests();

    /**
     * 允许最大UDP包大小
     */
    int getMaxUdpSize();

    /**
     * 获取分片大小
     */
    int getRangeSize();
}
