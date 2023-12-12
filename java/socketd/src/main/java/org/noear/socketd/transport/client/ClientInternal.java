package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.HeartbeatHandler;
import org.noear.socketd.transport.core.Processor;

/**
 * 客户端内部扩展接口
 *
 * @author noear
 * @since  2.1
 */
public interface ClientInternal extends Client {
    /**
     * 获取心跳处理
     */
    HeartbeatHandler heartbeatHandler();

    /**
     * 获取心跳间隔（毫秒）
     */
    long heartbeatInterval();

    /**
     * 获取配置
     */
    ClientConfig config();

    /**
     * 获取处理器
     */
    Processor processor();
}
