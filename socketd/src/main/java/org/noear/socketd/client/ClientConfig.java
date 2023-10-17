package org.noear.socketd.client;

import javax.net.ssl.SSLContext;

/**
 * 客记端配置（单位：毫秒）
 *
 * @author noear
 * @since 2.0
 */
public class ClientConfig {
    protected SSLContext sslContext;

    protected long heartbeatInterval;

    protected long connectTimeout;
    protected long readTimeout;
    protected long writeTimeout;

    protected int readBufferSize;
    protected int writeBufferSize;

    public ClientConfig() {
        connectTimeout = 3000;
        heartbeatInterval = 20 * 1000;
        readTimeout = 3000;
        writeTimeout = 3000;
    }


    public SSLContext getSslContext() {
        return sslContext;
    }

    /**
     * 心跳间隔
     */
    public long getHeartbeatInterval() {
        return heartbeatInterval;
    }

    /**
     * 连接超时
     */
    public long getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 读超时
     */
    public long getReadTimeout() {
        return readTimeout;
    }

    /**
     * 写超时
     */
    public long getWriteTimeout() {
        return writeTimeout;
    }

    /**
     * 读缓冲大小
     */
    public int getReadBufferSize() {
        return readBufferSize;
    }

    /**
     * 写缓冲大小
     */
    public int getWriteBufferSize() {
        return writeBufferSize;
    }
}
