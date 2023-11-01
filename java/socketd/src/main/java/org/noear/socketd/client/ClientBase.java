package org.noear.socketd.client;

import org.noear.socketd.core.HeartbeatHandler;
import org.noear.socketd.core.Listener;
import org.noear.socketd.core.ChannelAssistant;
import org.noear.socketd.core.Processor;
import org.noear.socketd.core.impl.ProcessorDefault;

import java.util.function.Consumer;

/**
 * 客户端基类
 *
 * @author noear
 * @since 2.0
 */
public abstract class ClientBase<T extends ChannelAssistant> implements Client {
    protected Processor processor = new ProcessorDefault();
    protected HeartbeatHandler heartbeatHandler;

    private final ClientConfig config;
    private final T assistant;

    public ClientBase(ClientConfig clientConfig, T assistant) {
        this.config = clientConfig;
        this.assistant = assistant;
    }

    /**
     * 配置
     */
    public ClientConfig config() {
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

    @Override
    public Client heartbeatHandler(HeartbeatHandler handler) {
        if (handler != null) {
            this.heartbeatHandler = handler;
        }

        return this;
    }

    public HeartbeatHandler heartbeatHandler() {
        return heartbeatHandler;
    }

    public long heartbeatInterval() {
        return config.getHeartbeatInterval();
    }

    @Override
    public Client config(Consumer<ClientConfig> consumer) {
        consumer.accept(config);
        return this;
    }

    @Override
    public Client listen(Listener listener) {
        processor.setListener(listener);
        return this;
    }
}