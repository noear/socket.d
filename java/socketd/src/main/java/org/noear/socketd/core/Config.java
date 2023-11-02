package org.noear.socketd.core;

import javax.net.ssl.SSLContext;
import java.nio.ByteBuffer;

/**
 * 配置类
 *
 * @author noear
 * @since 2.0
 */
public interface Config {
    /**
     * 获取协议架构
     */
    String getSchema();

    /**
     * 获取编解码器
     */
    Codec<ByteBuffer> getCodec();

    /**
     * 获取标识生成器
     */
    KeyGenerator getKeyGenerator();

    /**
     * 获取 ssl 上下文
     */
    SSLContext getSslContext();

    /**
     * 允许最大请求数
     */
    int getMaxRequests();
}