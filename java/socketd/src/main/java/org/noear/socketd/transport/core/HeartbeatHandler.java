package org.noear.socketd.transport.core;

/**
 * 心跳处理器
 *
 * @author noear
 * @since 2.0
 */
@FunctionalInterface
public interface HeartbeatHandler {
    /**
     * 心跳处理
     */
    void heartbeat(Session session) throws Exception;
}
