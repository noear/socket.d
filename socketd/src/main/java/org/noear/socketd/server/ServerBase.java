package org.noear.socketd.server;

import org.noear.socketd.protocol.Listener;
import org.noear.socketd.protocol.ChannelAssistant;
import org.noear.socketd.protocol.Processor;
import org.noear.socketd.protocol.impl.ProcessorDefault;

/**
 * @author noear
 * @since 2.0
 */
public abstract class ServerBase<T extends ChannelAssistant> implements Server {
    private Processor processor = new ProcessorDefault();

    private final ServerConfig config;

    private final T assistant;

    public ServerBase(ServerConfig config, T assistant) {
        this.config = config;
        this.assistant = assistant;
    }

    /**
     * 配置
     */
    public ServerConfig config() {
        return config;
    }

    /**
     * 通道助理
     */
    public T assistant() {
        return assistant;
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
