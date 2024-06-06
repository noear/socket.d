package org.noear.socketd.broker;

import org.noear.socketd.exception.SocketDException;
import org.noear.socketd.transport.core.Entity;

import java.io.IOException;

/**
 * 广播经纪人
 *
 * @author noear
 * @since 2.4
 */
public interface BroadcastBroker {
    /**
     * 广播
     *
     * @param event  事件
     * @param entity 实体（转发方式 https://socketd.noear.org/article/737 ）
     */
    void broadcast(String event, Entity entity) throws IOException, SocketDException;
}