package org.noear.socketd.client;

import org.noear.socketd.protocol.HeartbeatHandler;
import org.noear.socketd.protocol.Listener;
import org.noear.socketd.protocol.Session;

import java.util.function.Consumer;

/**
 * 客户端（用于构建会话）
 *
 * @author noear
 * @since 2.0
 */
public interface Client {
    /**
     * 心跳
     */
    Client heartbeatHandler(HeartbeatHandler handler);

    /**
     * 配置
     */
    Client config(Consumer<ClientConfig> consumer);

    /**
     * 监听
     */
    Client listen(Listener listener);

    /**
     * 打开
     */
    Session open() throws Exception;
}
