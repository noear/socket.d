package org.noear.socketd.transport.client;

import org.noear.socketd.core.*;
import org.noear.socketd.transport.core.ConfigBase;

import java.net.URI;

/**
 * 客记端配置（单位：毫秒）
 *
 * @author noear
 * @since 2.0
 */
public class ClientConfig extends ConfigBase<ClientConfig> {
    private final String schema;

    private final String url;
    private final URI uri;

    private long heartbeatInterval;

    private long connectTimeout;

    private int readBufferSize;
    private int writeBufferSize;

    private boolean autoReconnect;


    public ClientConfig(String url) {
        super(true);
        this.url = url;
        this.uri = URI.create(url);
        this.schema = uri.getScheme();

        this.connectTimeout = 3000;
        this.heartbeatInterval = 20 * 1000;

        this.autoReconnect = true;
    }


    /**
     * 获取协议架构
     */
    @Override
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
     * 获取连接主机
     */
    public String getHost() {
        return uri.getHost();
    }

    /**
     * 获取连接端口
     */
    public int getPort() {
        return uri.getPort();
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
     */
    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public ClientConfig autoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
        return this;
    }

    @Override
    public String toString() {
        return "ClientConfig{" +
                "schema='" + schema + '\'' +
                ", charset=" + charset +
                ", url='" + url + '\'' +
                ", heartbeatInterval=" + heartbeatInterval +
                ", connectTimeout=" + connectTimeout +
                ", readBufferSize=" + readBufferSize +
                ", writeBufferSize=" + writeBufferSize +
                ", autoReconnect=" + autoReconnect +
                ", maxRequests=" + maxRequests +
                ", maxUdpSize=" + maxUdpSize +
                '}';
    }
}