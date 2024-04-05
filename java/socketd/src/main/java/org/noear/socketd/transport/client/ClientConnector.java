package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.ChannelInternal;

import java.io.IOException;

/**
 * 客户端连接器
 *
 * @author noear
 * @since 2.0
 */
public interface ClientConnector {
    /**
     * 获取配置
     * */
    ClientConfig getConfig();

    /**
     * 是否支持自动重连
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
    void close() throws IOException;
}
