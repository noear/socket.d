package org.noear.socketd.client;

/**
 * @author noear 2023/10/17 created
 */
public class ClientConfig {
    private int connectTimeout;
    private int sendTimeout;

    public ClientConfig() {
        connectTimeout = 3000;
        sendTimeout = 3000;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getSendTimeout() {
        return sendTimeout;
    }
}
