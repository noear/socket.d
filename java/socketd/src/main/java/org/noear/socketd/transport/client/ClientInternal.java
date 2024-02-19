package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.Processor;

/**
 * 客户端内部接口
 *
 * @author noear
 * @since  2.1
 */
public interface ClientInternal extends Client {
    /**
     * 获取连接处理器
     */
    ClientConnectHandler getConnectHandler();

    /**
     * 获取心跳处理
     */
    ClientHeartbeatHandler getHeartbeatHandler();

    /**
     * 获取心跳间隔（毫秒）
     */
    long getHeartbeatInterval();

    /**
     * 获取配置
     */
    ClientConfig getConfig();

    /**
     * 获取处理器
     */
    Processor getProcessor();
}
