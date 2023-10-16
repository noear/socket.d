package org.noear.socketd.protocol;

import java.io.IOException;

/**
 * 心跳处理
 *
 * @author noear
 * @since 2.0
 */
public interface HeartbeatHandler {
    void handle(Session session) throws IOException;
}
