package org.noear.socketd.transport.core.listener;

import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;

import java.io.IOException;

/**
 * 消息处理者
 *
 * @author noear
 * @since 2.0
 */
@FunctionalInterface
public interface MessageHandler {
    void handle(Session session, Message message) throws IOException;
}
