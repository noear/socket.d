package org.noear.socketd.server;

import javax.net.ssl.SSLContext;

/**
 * 服务端属性（单位：毫秒）
 *
 * @author noear
 * @since 2.0
 */
public class ServerConfig {
    protected String host;
    protected int port;

    protected SSLContext sslContext;

    protected int coreThreads;
    protected int maxThreads;
    protected int idleTimeout;

    protected int readTimeout;
    protected int readBufferSize;
    protected int writeTimeout;
    protected int writeBufferSize;

    public ServerConfig() {
        host = "";
        port = 6329;
        coreThreads = Runtime.getRuntime().availableProcessors() * 2;
        maxThreads = coreThreads * 8;
        idleTimeout = 3000;
        readTimeout = 3000;
        writeTimeout = 3000;
    }


    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public int getCoreThreads() {
        return coreThreads;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getIdleTimeout() {
        return idleTimeout;
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
