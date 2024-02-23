package org.noear.socketd.transport.client.impl;

import org.noear.socketd.transport.client.ClientConnectHandler;
import org.noear.socketd.transport.client.ClientConnector;
import org.noear.socketd.transport.core.ChannelInternal;

import java.io.IOException;

/**
 * 客户端连接处理器
 *
 * @author noear
 * @since 2.4
 */
public class ClientConnectHandlerDefault implements ClientConnectHandler {
    /**
     * 连接处理
     */
    @Override
    public ChannelInternal clientConnect(ClientConnector connector) throws IOException{
        return connector.connect();
    }
}
