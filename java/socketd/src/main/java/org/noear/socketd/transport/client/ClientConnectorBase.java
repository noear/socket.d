package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.HeartbeatHandler;

/**
 * 客户端连接器基类
 *
 * @author noear
 * @since 2.0
 */
public abstract class ClientConnectorBase<T extends ClientInternal> implements ClientConnector{
    protected final T client;
    public ClientConnectorBase(T client){
        this.client = client;
    }

    /**
     * 心跳处理
     */
    @Override
    public HeartbeatHandler getHeartbeatHandler() {
        return client.getHeartbeatHandler();
    }

    /**
     * 心跳频率（单位：毫秒）
     */
    @Override
    public long getHeartbeatInterval() {
        return client.getHeartbeatInterval();
    }

    /**
     * 是否自动重连
     */
    @Override
    public boolean autoReconnect() {
        return client.getConfig().isAutoReconnect();
    }
}
