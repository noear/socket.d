package org.noear.socketd.client;

import org.noear.socketd.core.Channel;
import org.noear.socketd.core.HeartbeatHandler;

import java.io.Closeable;

/**
 * 客户端连接器
 *
 * @author noear
 * @since 2.0
 */
public interface ClientConnector extends Closeable {
    /**
     * 心跳处理
     */
    HeartbeatHandler heartbeatHandler();

    /**
     * 心跳频率（单位：毫秒）
     */
    long heartbeatInterval();

    /**
     * 是否自动重连
     */
    boolean autoReconnect();

    /**
     * 最大允许请求数（用于背压控制）
     */
    int maxRequests();

    /**
     * 连接
     *
     * @return 通道
     */
    Channel connect() throws Exception;
}
