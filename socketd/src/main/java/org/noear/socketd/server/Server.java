package org.noear.socketd.server;

import org.noear.socketd.protocol.Listener;
import org.noear.socketd.protocol.Processor;

import java.io.IOException;

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
    void start() throws IOException;

    /**
     * 停止
     */
    void stop() throws Exception;
}
