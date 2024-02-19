package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.ChannelInternal;

import java.io.IOException;

/**
 * 客户端连接处理器
 *
 * @author noear
 * @since 2.4
 */
@FunctionalInterface
public interface ClientConnectHandler {
    /**
     * 连接
     *
     * @param connector 连接器
     */
    ChannelInternal clientConnect(ClientConnector connector) throws IOException;
}
