package org.noear.socketd.protocol.impl;

import org.noear.socketd.protocol.HeartbeatHandler;
import org.noear.socketd.protocol.Session;

/**
 * 心跳默认处理
 *
 * @author noear
 * @since 2.0
 */
public class HeartbeatHandlerDefault implements HeartbeatHandler {

    @Override
    public void heartbeatHandle(Session session) {
        try {
            session.sendPing();
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }
}
