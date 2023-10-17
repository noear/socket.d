package org.noear.socketd.protocol.impl;

import org.noear.socketd.protocol.HeartbeatHandler;
import org.noear.socketd.protocol.Session;

/**
 * 默认心跳处理
 *
 * @author noear
 * @since 2.0
 */
public class HeartbeatHandlerDefault implements HeartbeatHandler {
    /**
     * 心跳处理
     * */
    @Override
    public void heartbeatHandle(Session session) {
        try {
            session.sendPing();
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }
}
