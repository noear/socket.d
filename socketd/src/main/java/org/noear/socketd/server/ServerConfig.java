package org.noear.socketd.server;

import javax.net.ssl.SSLContext;

/**
 * 服务端属性（单位：毫秒）
 *
 * @author noear
 * @since 2.0
 */
public class ServerConfig {
    private final String schema;

    private String host;
    private int port;

    private SSLContext sslContext;

    private int coreThreads;
    private int maxThreads;

    private long idleTimeout;

    private int readBufferSize;
    private int writeBufferSize;

    public ServerConfig(String schema) {
        this.schema = schema;

        host = "";
        port = 6329;

        coreThreads = Runtime.getRuntime().availableProcessors() * 2;
        maxThreads = coreThreads * 8;
        idleTimeout = 3000;

        readBufferSize = 512;
        writeBufferSize = 512;
    }

    /**
     * 获取协议架构
     */
    public String getSchema() {
        return schema;
    }

    /**
     * 获取主机
     */
    public String getHost() {
        return host;
    }

    /**
     * 配置主机
     */
    public ServerConfig host(String host) {
        this.host = host;
        return this;
    }

    /**
     * 获取端口
     */
    public int getPort() {
        return port;
    }

    /**
     * 配置端口
     */
    public ServerConfig port(int port) {
        this.port = port;
        return this;
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
    public ServerConfig sslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    /**
     * 获取核心线程数
     */
    public int getCoreThreads() {
        return coreThreads;
    }

    /**
     * 配置核心线程数
     */
    public ServerConfig coreThreads(int coreThreads) {
        this.coreThreads = coreThreads;
        return this;
    }

    /**
     * 获取最大线程数
     */
    public int getMaxThreads() {
        return maxThreads;
    }

    /**
     * 配置最大线程数
     */
    public ServerConfig maxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        return this;
    }

    /**
     * 获取线程空闲超时
     */
    public long getIdleTimeout() {
        return idleTimeout;
    }

    /**
     * 配置线程空闲超时
     */
    public ServerConfig idleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
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
    public ServerConfig readBufferSize(int readBufferSize) {
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
    public ServerConfig writeBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
        return this;
    }
}