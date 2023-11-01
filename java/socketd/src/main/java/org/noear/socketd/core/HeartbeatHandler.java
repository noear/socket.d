package org.noear.socketd.core;

import java.io.IOException;

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
    void heartbeatHandle(Session session) throws Exception;
}
