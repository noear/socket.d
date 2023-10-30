package org.noear.socketd.server;

import javax.net.ssl.SSLContext;

/**
 * 服务端属性（单位：毫秒）
 *
 * @author noear
 * @since 2.0
 */
public class ServerConfig {
    protected final String schema;

    protected String host;
    protected int port;

    protected SSLContext sslContext;

    protected int coreThreads;
    protected int maxThreads;

    protected long idleTimeout;
    protected long readTimeout;
    protected long writeTimeout;

    protected int readBufferSize;
    protected int writeBufferSize;

    public ServerConfig(String schema) {
        this.schema = schema;

        host = "";
        port = 6329;

        coreThreads = Runtime.getRuntime().availableProcessors() * 2;
        maxThreads = coreThreads * 8;
        idleTimeout = 3000;

        readTimeout = 3000;
        writeTimeout = 3000;

        readBufferSize = 512;
        writeBufferSize = 512;
    }

    public String getSchema() {
        return schema;
    }

    /**
     * 端口
     */
    public int getPort() {
        return port;
    }

    /**
     * 主题
     */
    public String getHost() {
        return host;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    /**
     * 核心线程数
     */
    public int getCoreThreads() {
        return coreThreads;
    }

    /**
     * 最大线程数
     */
    public int getMaxThreads() {
        return maxThreads;
    }

    /**
     * 空闲超时
     */
    public long getIdleTimeout() {
        return idleTimeout;
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
