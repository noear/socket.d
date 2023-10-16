package org.noear.socketd.client;

import org.noear.socketd.protocol.HeartbeatHandler;
import org.noear.socketd.protocol.Listener;
import org.noear.socketd.protocol.Session;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

/**
 * 连接器
 *
 * @author noear
 * @since 2.0
 */
public interface Client {
    /**
     * 连接地址
     */
    URI uri();
    Client url(String url);

    /**
     * 是否自动重连
     */
    boolean autoReconnect();

    /**
     * 自动重链
     * */
    Client autoReconnect(boolean enable);

    Client ssl(SSLContext sslContext);

    /**
     * 心跳
     * */
    Client heartbeat(HeartbeatHandler handler);

    /**
     * 监听
     */
    Client listen(Listener listener);

    /**
     * 打开
     */
    Session open() throws IOException, TimeoutException;
}
