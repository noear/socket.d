package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.HeartbeatHandler;
import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Processor;
import org.noear.socketd.transport.core.Session;

import java.io.IOException;
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
    Client config(ClientConfigHandler consumer);

    /**
     * 处理
     */
    Client process(Processor processor);

    /**
     * 监听
     */
    Client listen(Listener listener);

    /**
     * 打开会话
     */
    Session open() throws IOException;
}
