package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.Session;

/**
 * 客户端心跳处理器
 *
 * @author noear
 * @since 2.0
 */
@FunctionalInterface
public interface ClientHeartbeatHandler {
    /**
     * 心跳处理
     */
    void clientHeartbeat(Session session) throws Exception;
}
