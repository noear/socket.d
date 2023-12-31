package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.HeartbeatHandler;

import java.io.IOException;

/**
 * 客户端连接器
 *
 * @author noear
 * @since 2.0
 */
public interface ClientConnector {
    /**
     * 获取心跳处理
     */
    HeartbeatHandler getHeartbeatHandler();

    /**
     * 获取心跳频率（单位：毫秒）
     */
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
    ChannelInternal connect() throws IOException;

    /**
     * 关闭
     */
    void close();
}
