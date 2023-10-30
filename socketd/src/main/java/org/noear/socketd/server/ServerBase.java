package org.noear.socketd.server;

import org.noear.socketd.protocol.Listener;
import org.noear.socketd.protocol.OutputTarget;
import org.noear.socketd.protocol.Processor;
import org.noear.socketd.protocol.impl.ProcessorDefault;

/**
 * @author noear
 * @since 2.0
 */
public abstract class ServerBase<T extends OutputTarget> implements Server {
    private Processor processor = new ProcessorDefault();

    private final ServerConfig config;

    private final T exchanger;

    public ServerBase(ServerConfig config, T exchanger) {
        this.config = config;
        this.exchanger = exchanger;
    }

    /**
     * 配置
     */
    public ServerConfig config() {
        return config;
    }

    /**
     * 交换机
     */
    public T exchanger() {
        return exchanger;
    }

    /**
     * 处理器
     */
    public Processor processor() {
        return processor;
    }

    /**
     * 处理
     */
    @Override
    public void process(Processor processor) {
        if (processor != null) {
            this.processor = processor;
        }
    }

    /**
     * 监听
     */
    @Override
    public void listen(Listener listener) {
        processor.setListener(listener);
    }
}
