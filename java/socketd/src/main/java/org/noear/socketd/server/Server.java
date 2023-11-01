package org.noear.socketd.server;

import org.noear.socketd.core.Listener;
import org.noear.socketd.core.Processor;

/**
 * 服务端
 *
 * @author noear
 * @since 2.0
 */
public interface Server {
    /**
     * 处理
     * */
    void process(Processor processor);
    /**
     * 监听
     */
    void listen(Listener listener);

    /**
     * 启动
     */
    void start() throws Exception;

    /**
     * 停止
     */
    void stop() throws Exception;
}
