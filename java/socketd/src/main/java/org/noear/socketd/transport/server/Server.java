package org.noear.socketd.transport.server;

import org.noear.socketd.transport.core.Listener;

import java.io.IOException;

/**
 * 服务端
 *
 * @author noear
 * @since 2.0
 */
public interface Server {
    /**
     * 获取台头
     * */
    String getTitle();

    /**
     * 获取配置
     * */
    ServerConfig getConfig();

    /**
     * 配置
     */
    Server config(ServerConfigHandler configHandler);

    /**
     * 监听
     */
    Server listen(Listener listener);

    /**
     * 启动
     */
    Server start() throws IOException;

    /**
     * 预停止
     * */
    void prestop();

    /**
     * 停止
     */
    void stop();
}
