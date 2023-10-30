package org.noear.socketd.client;

import javax.net.ssl.SSLContext;

/**
 * 客记端配置（单位：毫秒）
 *
 * @author noear
 * @since 2.0
 */
public class ClientConfig {
    private final String schema;

    private SSLContext sslContext;

    private long heartbeatInterval;

    private long connectTimeout;
    private long writeTimeout;

    private int readBufferSize;
    private int writeBufferSize;

    public ClientConfig(String schema) {
        this.schema = schema;

        connectTimeout = 3000;
        heartbeatInterval = 20 * 1000;
        writeTimeout = 3000;
    }

    public String getSchema() {
        return schema;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public ClientConfig sslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    /**
     * 心跳间隔
     */
    public long getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public ClientConfig heartbeatInterval(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
        return this;
    }

    /**
     * 连接超时
     */
    public long getConnectTimeout() {
        return connectTimeout;
    }

    public ClientConfig connectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * 写超时
     */
    public long getWriteTimeout() {
        return writeTimeout;
    }

    public ClientConfig writeTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    /**
     * 读缓冲大小
     */
    public int getReadBufferSize() {
        return readBufferSize;
    }

    public ClientConfig readBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
        return this;
    }

    /**
     * 写缓冲大小
     */
    public int getWriteBufferSize() {
        return writeBufferSize;
    }

    public ClientConfig writeBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
        return this;
    }
}
