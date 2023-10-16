package org.noear.socketd.protocol.impl;

import org.noear.socketd.protocol.HeartbeatHandler;
import org.noear.socketd.protocol.Session;

import java.io.IOException;

/**
 * @author noear 2023/10/17 created
 */
public class HeartbeatHandlerDefault implements HeartbeatHandler {
    @Override
    public void handle(Session session) throws IOException {
        session.sendPing();
    }
}
