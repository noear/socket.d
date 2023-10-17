package org.noear.socketd.client;

import org.noear.socketd.protocol.Channel;

import java.io.Closeable;
import java.io.IOException;

/**
 * 客户端连接器
 *
 * @author noear
 * @since 2.0
 */
public interface ClientConnector extends Closeable {
    /**
     * 是否自动重连
     */
    boolean autoReconnect();

    /**
     * 连接
     */
    Channel connect() throws IOException;
}
