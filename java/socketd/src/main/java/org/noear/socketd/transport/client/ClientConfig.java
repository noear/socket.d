package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.core.impl.ConfigBase;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 客记端配置（单位：毫秒）
 *
 * @author noear
 * @since 2.0
 */
public class ClientConfig extends ConfigBase<ClientConfig> {
    //协议架构（tcp, ws, udp, ...）
    private final String schema;
    private final String schemaCleaned;

    //连接地址
    private final String linkUrl;
    private final String url;
    private final String host;
    private final int port;
    private final Map<String, String> metaMap = new LinkedHashMap<>();

    //心跳间隔（毫秒）
    private long heartbeatInterval;

    //连接越时（毫秒）
    private long connectTimeout;

    //是否自动重链
    private boolean autoReconnect;

    public ClientConfig(String url) {
        super(true);

        int idx = url.indexOf("://");
        if (idx < 2) {
            throw new IllegalArgumentException("The serverUrl invalid: " + url);
        }

        this.schema = url.substring(0, idx);

        //支持 sd: 开头的架构
        if (url.startsWith("sd:")) {
            url = url.substring(3);
        }

        URI uri = URI.create(url);

        this.linkUrl = "sd:" + url;
        this.url = url;
        this.host = uri.getHost();
        this.port = (uri.getPort() < 0 ? Constants.DEF_PORT : uri.getPort());
        this.schemaCleaned = uri.getScheme();

        this.connectTimeout = 10_000;
        this.heartbeatInterval = 20_000;

        this.autoReconnect = true;
    }


    /**
     * 获取链接地址
     */
    public String getLinkUrl() {
        return linkUrl;
    }

    /**
     * 获取连接地址
     */
    public String getUrl() {
        return url;
    }

    /**
     * 获取协议架构（用于查找供应者）
     */
    public String getSchema() {
        return schema;
    }


    /**
     * 获取连接主机
     */
    public String getHost() {
        return host;
    }

    /**
     * 获取连接端口
     */
    public int getPort() {
        return port;
    }

    /**
     * 获取连接元信息字典
     */
    public Map<String, String> getMetaMap() {
        return metaMap;
    }

    /**
     * 配置连接元信息
     * */
    public ClientConfig metaPut(String name, String val){
        metaMap.put(name, val);
        return this;
    }

    /**
     * 获取心跳间隔（单位毫秒）
     */
    public long getHeartbeatInterval() {
        return heartbeatInterval;
    }

    /**
     * 配置心跳间隔（单位毫秒）
     */
    public ClientConfig heartbeatInterval(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
        return this;
    }

    /**
     * 获取连接超时（单位毫秒）
     */
    public long getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 配置连接超时（单位毫秒）
     */
    public ClientConfig connectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * 获取是否自动重链
     */
    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    /**
     * 配置是否自动重链
     */
    public ClientConfig autoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
        return this;
    }

    @Override
    public ClientConfig idleTimeout(int idleTimeout) {
        if (autoReconnect == false) {
            //自动重链下，禁用 idleTimeout
            return super.idleTimeout(idleTimeout);
        } else {
            return super.idleTimeout(0);
        }
    }

    @Override
    public String toString() {
        return "ClientConfig{" +
                "schema='" + schemaCleaned + '\'' +
                ", charset=" + charset +
                ", url='" + url + '\'' +
                ", ioThreads=" + ioThreads +
                ", codecThreads=" + codecThreads +
                ", exchangeThreads=" + workThreads +
                ", heartbeatInterval=" + heartbeatInterval +
                ", connectTimeout=" + connectTimeout +
                ", idleTimeout=" + idleTimeout +
                ", requestTimeout=" + requestTimeout +
                ", streamTimeout=" + streamTimeout +
                ", readBufferSize=" + readBufferSize +
                ", writeBufferSize=" + writeBufferSize +
                ", autoReconnect=" + autoReconnect +
                ", maxUdpSize=" + maxUdpSize +
                '}';
    }
}