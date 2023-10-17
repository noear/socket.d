package org.noear.socketd.client;

/**
 * 客记端配置
 *
 * @author noear
 * @since 2.0
 */
public class ClientConfig {
    private int connectTimeout;

    protected int readTimeout;
    protected int readBufferSize;
    protected int writeTimeout;
    protected int writeBufferSize;

    public ClientConfig() {
        connectTimeout = 3000;
        readTimeout = 3000;
        writeTimeout = 3000;
    }

    public int getConnectTimeout() {
        return connectTimeout;
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
