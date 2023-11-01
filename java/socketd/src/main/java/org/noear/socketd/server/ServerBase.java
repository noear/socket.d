package org.noear.socketd.server;

import org.noear.socketd.core.Listener;
import org.noear.socketd.core.ChannelAssistant;
import org.noear.socketd.core.Processor;
import org.noear.socketd.core.impl.ProcessorDefault;

import java.util.function.Consumer;

/**
 * 服务端基类
 *
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
     * 获取通道助理
     */
    public T assistant() {
        return assistant;
    }

    /**
     * 获取配置
     */
    public ServerConfig config() {
        return config;
    }

    /**
     * 配置
     */
    public Server config(Consumer<ServerConfig> consumer) {
        consumer.accept(config);
        return this;
    }


    /**
     * 获取处理器
     */
    public Processor processor() {
        return processor;
    }

    /**
     * 设置处理器
     */
    @Override
    public Server process(Processor processor) {
        if (processor != null) {
            this.processor = processor;
        }

        return this;
    }

    /**
     * 设置监听器
     */
    @Override
    public Server listen(Listener listener) {
        processor.setListener(listener);
        return this;
    }
}
