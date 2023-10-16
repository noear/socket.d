package org.noear.socketd.server;

/**
 * 服务端属性
 *
 * @author noear
 * @since 2.0
 */
public class ServerConfig {
    protected String host;
    protected int port;
    protected int coreThreads;
    protected int maxThreads;
    protected int idleTimeout;

    public ServerConfig() {
        host = "";
        port = 6329;
        coreThreads = Runtime.getRuntime().availableProcessors() * 2;
        maxThreads = coreThreads * 8;
        idleTimeout = 3000;
    }


    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
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
}
