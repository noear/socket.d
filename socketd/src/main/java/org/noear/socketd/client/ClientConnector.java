package org.noear.socketd.client;

import org.noear.socketd.protocol.Channel;

import java.io.Closeable;
import java.io.IOException;

/**
 * 客户端
 *
 * @author noear
 * @since 2.0
 */
public interface ClientConnector extends Closeable {
    boolean autoReconnect();
    Channel connect() throws IOException;
}
