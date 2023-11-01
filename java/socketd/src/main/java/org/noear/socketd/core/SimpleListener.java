package org.noear.socketd.core;

import java.io.IOException;

/**
 * 简单的监听器（一般用于占位）
 *
 * @author noear
 * @since 2.0
 */
public class SimpleListener implements Listener {
    /**
     * 打开时
     *
     * @param session 会话
     */
    @Override
    public void onOpen(Session session) throws IOException{

    }

    /**
     * 收到消息时
     *
     * @param session 会话
     * @param message 消息
     */
    @Override
    public void onMessage(Session session, Message message) throws IOException {

    }

    /**
     * 关闭时
     *
     * @param session 会话
     */
    @Override
    public void onClose(Session session) {

    }

    /**
     * 出错时
     *
     * @param session 会话
     * @param error   错误信息
     */
    @Override
    public void onError(Session session, Throwable error) {

    }
}
