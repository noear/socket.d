package org.noear.socketd.client;

import org.noear.socketd.protocol.HeartbeatHandler;
import org.noear.socketd.protocol.Listener;
import org.noear.socketd.protocol.OutputTarget;
import org.noear.socketd.protocol.Processor;
import org.noear.socketd.protocol.impl.ProcessorDefault;

import java.net.URI;
import java.util.function.Consumer;

/**
 * 客户端基类
 *
 * @author noear
 * @since 2.0
 */
public abstract class ClientBase<T extends OutputTarget> implements Client {
    protected Processor processor = new ProcessorDefault();
    protected HeartbeatHandler heartbeatHandler;

    private final ClientConfig config;
    private final T exchanger;

    public ClientBase(ClientConfig clientConfig, T exchanger) {
        this.config = clientConfig;
        this.exchanger = exchanger;
    }

    /**
     * 配置
     */
    public ClientConfig config() {
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