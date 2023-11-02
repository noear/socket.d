package org.noear.socketd.server;

import org.noear.socketd.core.Listener;
import org.noear.socketd.core.Processor;

import java.util.function.Consumer;

/**
 * 服务端
 *
 * @author noear
 * @since 2.0
 */
public interface Server {
    /**
     * 配置
     */
    Server config(Consumer<ServerConfig> consumer);

    /**
     * 处理
     */
    Server process(Processor processor);

    /**
     * 监听
     */
    Server listen(Listener listener);

    /**
     * 启动
     */
    Server start() throws Exception;

    /**
     * 停止
     */
    void stop();
}
