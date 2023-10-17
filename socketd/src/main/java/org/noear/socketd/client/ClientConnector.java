package org.noear.socketd.client;

import org.noear.socketd.protocol.Channel;
import org.noear.socketd.protocol.HeartbeatHandler;

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
     * 心跳处理
     * */
    HeartbeatHandler heartbeatHandler();

    /**
     * 心跳频率（单位：毫秒）
     * */
    long getHeartbeatInterval();

    /**
     * 是否自动重连
     */
    boolean autoReconnect();

    /**
     * 连接
     *
     * @return 通道
     */
    Channel connect() throws IOException;
}
