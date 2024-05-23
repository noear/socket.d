package org.noear.socketd.broker;

import org.noear.socketd.transport.core.Message;

import java.io.IOException;

/**
 * 广播经纪人
 *
 * @author noear
 * @since 2.4
 */
public interface BroadcastBroker {
    /**
     * 转发
     *
     * @param message 消息
     * @param atName  目标（转发方式 https://socketd.noear.org/article/737 ）
     */
    void forwardTo(Message message, String atName) throws IOException;
}