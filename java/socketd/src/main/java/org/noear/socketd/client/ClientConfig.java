package org.noear.socketd.client;

import javax.net.ssl.SSLContext;
import java.net.URI;

/**
 * 客记端配置（单位：毫秒）
 *
 * @author noear
 * @since 2.0
 */
public class ClientConfig {
    private final String url;
    private final URI uri;
    private final String schema;

    private SSLContext sslContext;

    private long heartbeatInterval;

    private long connectTimeout;

    private int readBufferSize;
    private int writeBufferSize;

    private boolean autoReconnect;

    private int maxRequests;

    public ClientConfig(String url) {
        this.url = url;
        this.uri = URI.create(url);
        this.schema = uri.getScheme();

        connectTimeout = 3000;
        heartbeatInterval = 20 * 1000;

        maxRequests = 10;
    }


    /**
     * 获取协议架构
     */
    public String getSchema() {
        return schema;
    }

    /**
     * 获取连接地址
     */
    public String getUrl() {
        return url;
    }

    /**
     * 获取连接地址
     */
    public URI getUri() {
        return uri;
    }

    /**
     * 获取 ssl 上下文
     */
    public SSLContext getSslContext() {
        return sslContext;
    }

    /**
     * 配置 ssl 上下文
     */
    public ClientConfig sslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    /**
     * 获取心跳间隔
     */
    public long getHeartbeatInterval() {
        return heartbeatInterval;
    }

    /**
     * 配置心跳间隔
     */
    public ClientConfig heartbeatInterval(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
        return this;
    }

    /**
     * 获取连接超时
     */
    public long getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 配置连接超时
     */
    public ClientConfig connectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
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
    public ClientConfig readBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
        return this;
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
    public ClientConfig writeBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
        return this;
    }

    /**
     * 是否自动重链
     * */
    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public ClientConfig autoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
        return this;
    }

    /**
     * 允许最大请求数
     * */
    public int getMaxRequests() {
        return maxRequests;
    }

    public ClientConfig maxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
        return this;
    }
}