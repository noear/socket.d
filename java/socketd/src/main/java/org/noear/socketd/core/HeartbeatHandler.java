package org.noear.socketd.core;

/**
 * 心跳处理器
 *
 * @author noear
 * @since 2.0
 */
public interface HeartbeatHandler {
    /**
     * 心跳处理
     */
    void heartbeatHandle(Session session);
}
