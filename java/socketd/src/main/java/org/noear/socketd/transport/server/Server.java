package org.noear.socketd.transport.server;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Processor;

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
    String title();

    /**
     * 获取配置
     * */
    ServerConfig config();

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
     * 停止
     */
    void stop();
}
