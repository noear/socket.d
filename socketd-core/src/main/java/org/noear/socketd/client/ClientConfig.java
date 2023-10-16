package org.noear.socketd.client;

/**
 * @author noear 2023/10/17 created
 */
public class ClientConfig {
    private int connectTimeout;
    private int readTimeout;
    private int writeTimeout;

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

    public int getWriteTimeout() {
        return writeTimeout;
    }
}
