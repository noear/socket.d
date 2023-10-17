package org.noear.socketd.client;

import javax.net.ssl.SSLContext;

/**
 * 客记端配置（单位：毫秒）
 *
 * @author noear
 * @since 2.0
 */
public class ClientConfig {
    protected int connectTimeout;
    protected SSLContext sslContext;

    protected int  heartbeatInterval;

    protected int readTimeout;
    protected int readBufferSize;
    protected int writeTimeout;
    protected int writeBufferSize;

    public ClientConfig() {
        connectTimeout = 3000;
        heartbeatInterval = 20 * 1000;
        readTimeout = 3000;
        writeTimeout = 3000;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public int getWriteBufferSize() {
        return writeBufferSize;
    }


}
