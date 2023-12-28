package org.noear.socketd.transport.server;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.ChannelAssistant;
import org.noear.socketd.transport.core.Processor;
import org.noear.socketd.transport.core.internal.ProcessorDefault;

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
    protected boolean isStarted;

    public ServerBase(ServerConfig config, T assistant) {
        this.config = config;
        this.assistant = assistant;
    }

    /**
     * 获取通道助理
     */
    public T getAssistant() {
        return assistant;
    }

    /**
     * 获取配置
     */
    @Override
    public ServerConfig getConfig() {
        return config;
    }

    /**
     * 配置
     */
    public Server config(ServerConfigHandler configHandler) {
        if (configHandler != null) {
            configHandler.serverConfig(config);
        }
        return this;
    }


    /**
     * 获取处理器
     */
    public Processor getProcessor() {
        return processor;
    }


    /**
     * 设置监听器
     */
    @Override
    public Server listen(Listener listener) {
        if (listener != null) {
            processor.setListener(listener);
        }
        return this;
    }
}
