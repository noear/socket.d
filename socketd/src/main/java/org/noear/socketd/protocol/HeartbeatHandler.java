package org.noear.socketd.protocol;

/**
 * 心跳处理
 *
 * @author noear
 * @since 2.0
 */
public interface HeartbeatHandler {
    void heartbeatHandle(Session session);
}
