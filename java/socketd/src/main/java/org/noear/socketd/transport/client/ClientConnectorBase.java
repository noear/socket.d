package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.HeartbeatHandler;

/**
 * 客户端连接器基类
 *
 * @author noear
 * @since 2.0
 */
public abstract class ClientConnectorBase<T extends ClientBase> implements ClientConnector{
    protected final T client;
    public ClientConnectorBase(T client){
        this.client = client;
    }

    /**
     * 心跳处理
     */
    @Override
    public HeartbeatHandler heartbeatHandler() {
        return client.heartbeatHandler();
    }

    /**
     * 心跳频率（单位：毫秒）
     */
    @Override
    public long heartbeatInterval() {
        return client.heartbeatInterval();
    }

    /**
     * 是否自动重连
     */
    @Override
    public boolean autoReconnect() {
        return client.config().isAutoReconnect();
    }

    /**
     * 最大允许请求数（用于背压控制）
     */
    @Override
    public int maxRequests() {
        return client.config().getMaxRequests();
    }
}
