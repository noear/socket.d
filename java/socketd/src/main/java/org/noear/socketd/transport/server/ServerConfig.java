package org.noear.socketd.transport.server;

import org.noear.socketd.transport.core.impl.ConfigBase;
import org.noear.socketd.utils.StrUtils;


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

    public ServerConfig(String schema) {
        super(false);
        //支持 sd: 开头的架构
        if(schema.startsWith("sd:")){
            schema = schema.substring(3);
        }

        this.schema = schema;

        this.host = "";
        this.port = 8602;
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
        if (StrUtils.isEmpty(host)) {
            return "sd:" + schema + "://127.0.0.1:" + port;
        } else {
            return "sd:" + schema + "://" + host + ":" + port;
        }
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
                ", replyTimeout=" + requestTimeout +
                ", readBufferSize=" + readBufferSize +
                ", writeBufferSize=" + writeBufferSize +
                ", maxUdpSize=" + maxUdpSize +
                '}';
    }
}