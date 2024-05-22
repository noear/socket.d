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
    //协议架构（tcp, ws, udp, ...）
    private final String schema;
    private final String schemaCleaned;

    //主机名
    private String host;
    //端口
    private int port;

    public ServerConfig(String schema) {
        super(false);

        this.schema = schema;

        //支持 sd: 开头的架构
        if(schema.startsWith("sd:")){
            schema = schema.substring(3);
        }

        this.schemaCleaned = schema;

        this.host = "";
        this.port = 8602;
    }

    /**
     * 获取协议架构（用于查找供应者）
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
            return "sd:" + schemaCleaned + "://127.0.0.1:" + port;
        } else {
            return "sd:" + schemaCleaned + "://" + host + ":" + port;
        }
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "schema='" + schemaCleaned + '\'' +
                ", charset=" + charset +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", ioThreads=" + ioThreads +
                ", codecThreads=" + codecThreads +
                ", exchangeThreads=" + workThreads +
                ", idleTimeout=" + idleTimeout +
                ", requestTimeout=" + requestTimeout +
                ", streamTimeout=" + streamTimeout +
                ", readBufferSize=" + readBufferSize +
                ", writeBufferSize=" + writeBufferSize +
                ", maxUdpSize=" + maxUdpSize +
                '}';
    }
}