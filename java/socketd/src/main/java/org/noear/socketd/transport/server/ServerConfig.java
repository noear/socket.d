package org.noear.socketd.transport.server;

import org.noear.socketd.transport.core.ConfigBase;
import org.noear.socketd.utils.Utils;


/**
 * 服务端属性（单位：毫秒）
 *
 * @author noear
 * @since 2.0
 */
public class ServerConfig extends ConfigBase<ServerConfig> {
    private final String schema;

    //主机名
    private String host;
    //端口
    private int port;

    //读缓冲大小
    private int readBufferSize;
    //写缓冲大小
    private int writeBufferSize;

    public ServerConfig(String schema) {
        super(false);
        this.schema = schema;

        this.host = "";
        this.port = 8602;


        this.idleTimeout = 60 * 1000L;

        this.readBufferSize = 512;
        this.writeBufferSize = 512;
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
     * 获取本机地址
     */
    public String getLocalUrl() {
        if (Utils.isEmpty(host)) {
            return schema + "://127.0.0.1:" + port;
        } else {
            return schema + "://" + host + ":" + port;
        }
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

    @Override
    public String toString() {
        return "ServerConfig{" +
                "schema='" + schema + '\'' +
                ", charset=" + charset +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", coreThreads=" + coreThreads +
                ", maxThreads=" + maxThreads +
                ", idleTimeout=" + idleTimeout +
                ", replyTimeout=" + replyTimeout +
                ", readBufferSize=" + readBufferSize +
                ", writeBufferSize=" + writeBufferSize +
                ", maxRequests=" + maxRequests +
                ", maxUdpSize=" + maxUdpSize +
                '}';
    }
}