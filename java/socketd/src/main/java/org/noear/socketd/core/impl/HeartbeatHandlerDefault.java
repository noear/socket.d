package org.noear.socketd.core.impl;

import org.noear.socketd.core.HeartbeatHandler;
import org.noear.socketd.core.Session;
import org.noear.socketd.exception.SocketdException;

import java.io.IOException;

/**
 * 默认心跳处理
 *
 * @author noear
 * @since 2.0
 */
public class HeartbeatHandlerDefault implements HeartbeatHandler {
    /**
     * 心跳处理
     */
    @Override
    public void heartbeatHandle(Session session) throws Exception {
        session.sendPing();
    }
}
