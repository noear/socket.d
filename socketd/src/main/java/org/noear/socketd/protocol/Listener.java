package org.noear.socketd.protocol;

import java.io.IOException;

/**
 * 监听器
 *
 * @author noear
 * @since 2.0
 */
public interface Listener {
    /**
     * 打开时
     */
    void onOpen(Session session) throws IOException;

    /**
     * 收到消息时
     */
    void onMessage(Session session, Message message) throws IOException;

    /**
     * 关闭时
     */
    void onClose(Session session);

    /**
     * 出错时
     */
    void onError(Session session, Throwable error);
}
