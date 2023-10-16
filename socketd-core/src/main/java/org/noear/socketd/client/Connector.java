package org.noear.socketd.client;

import org.noear.socketd.protocol.Listener;
import org.noear.socketd.protocol.Session;

import java.io.IOException;
import java.net.URI;

/**
 * 连接器
 *
 * @author noear
 * @since 2.0
 */
public interface Connector {
    /**
     * 连接地址
     */
    URI uri();

    /**
     * 是否自动重连
     */
    boolean autoReconnect();

    /**
     * 监听
     */
    Connector listen(Listener listener);

    /**
     * 打开
     */
    Session open() throws IOException;
}
