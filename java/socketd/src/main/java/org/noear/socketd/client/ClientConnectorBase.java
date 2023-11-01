package org.noear.socketd.client;

import org.noear.socketd.core.HeartbeatHandler;

/**
 * @author noear
 * @since 2.0
 */
public abstract class ClientConnectorBase<T extends ClientBase> implements ClientConnector{
    protected final T client;
    public ClientConnectorBase(T client){
        this.client = client;
    }

    @Override
    public HeartbeatHandler heartbeatHandler() {
        return client.heartbeatHandler();
    }

    @Override
    public long heartbeatInterval() {
        return client.heartbeatInterval();
    }

    @Override
    public boolean autoReconnect() {
        return client.config().isAutoReconnect();
    }

    @Override
    public int maxRequests() {
        return client.config().getMaxRequests();
    }
}
