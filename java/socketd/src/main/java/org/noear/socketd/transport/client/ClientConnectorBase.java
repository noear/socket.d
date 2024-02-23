package org.noear.socketd.transport.client;

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
     * 获取配置
     * */
    @Override
    public ClientConfig getConfig(){
        return client.getConfig();
    }


    /**
     * 是否支持自动重连
     */
    @Override
    public boolean autoReconnect() {
        return client.getConfig().isAutoReconnect();
    }
}
