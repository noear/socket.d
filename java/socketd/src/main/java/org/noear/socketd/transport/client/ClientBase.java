package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.HeartbeatHandler;
import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.ChannelAssistant;
import org.noear.socketd.transport.core.Processor;
import org.noear.socketd.transport.core.impl.ProcessorDefault;

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
     * 获取通道助理
     */
    public T assistant() {
        return assistant;
    }

    /**
     * 获取心跳处理
     */
    public HeartbeatHandler heartbeatHandler() {
        return heartbeatHandler;
    }

    /**
     * 获取心跳间隔（毫秒）
     */
    public long heartbeatInterval() {
        return config.getHeartbeatInterval();
    }


    /**
     * 获取配置
     */
    public ClientConfig config() {
        return config;
    }

    /**
     * 获取处理器
     */
    public Processor processor() {
        return processor;
    }

    /**
     * 设置心跳
     */
    @Override
    public Client heartbeatHandler(HeartbeatHandler handler) {
        if (handler != null) {
            this.heartbeatHandler = handler;
        }

        return this;
    }

    /**
     * 配置
     */
    @Override
    public Client config(Consumer<ClientConfig> consumer) {
        consumer.accept(config);
        return this;
    }


    /**
     * 设置处理器
     */
    @Override
    public Client process(Processor processor) {
        if (processor != null) {
            this.processor = processor;
        }

        return this;
    }

    /**
     * 设置监听器
     */
    @Override
    public Client listen(Listener listener) {
        processor.setListener(listener);
        return this;
    }
}