package org.noear.socketd.transport.client.impl;

import org.noear.socketd.transport.client.ClientHeartbeatHandler;
import org.noear.socketd.transport.core.Session;

/**
 * 心跳处理默认实现
 *
 * @author noear
 * @since 2.0
 */
public class ClientHeartbeatHandlerDefault implements ClientHeartbeatHandler {
    /**
     * 心跳处理
     */
    @Override
    public void clientHeartbeat(Session session) throws Exception {
        session.sendPing();
    }
}
