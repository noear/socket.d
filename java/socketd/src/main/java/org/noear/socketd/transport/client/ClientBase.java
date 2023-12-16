package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.internal.ProcessorDefault;
import org.noear.socketd.transport.core.internal.SessionDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 客户端基类
 *
 * @author noear
 * @since 2.0
 */
public abstract class ClientBase<T extends ChannelAssistant> implements ClientInternal {
    private static final Logger log = LoggerFactory.getLogger(ClientBase.class);

    //协议处理器
    protected Processor processor = new ProcessorDefault();
    //心跳处理
    protected HeartbeatHandler heartbeatHandler;

    //配置
    private final ClientConfig config;
    //助理
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
    @Override
    public HeartbeatHandler heartbeatHandler() {
        return heartbeatHandler;
    }

    /**
     * 获取心跳间隔（毫秒）
     */
    @Override
    public long heartbeatInterval() {
        return config.getHeartbeatInterval();
    }


    /**
     * 获取配置
     */
    @Override
    public ClientConfig config() {
        return config;
    }

    /**
     * 获取处理器
     */
    @Override
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
    public Client config(ClientConfigHandler configHandler) {
        if (configHandler != null) {
            configHandler.clientConfig(config);
        }
        return this;
    }

    /**
     * 设置监听器
     */
    @Override
    public Client listen(Listener listener) {
        if (listener != null) {
            processor.setListener(listener);
        }
        return this;
    }

    /**
     * 打开会话
     */
    @Override
    public Session open() throws IOException {
        ClientConnector connector = createConnector();

        //连接
        ChannelInternal channel0 = connector.connect();
        //新建客户端通道
        ClientChannel clientChannel = new ClientChannel(channel0, connector);
        //同步握手信息
        clientChannel.setHandshake(channel0.getHandshake());
        Session session = new SessionDefault(clientChannel);
        //原始通道切换为带壳的 session
        channel0.setSession(session);

        log.info("Socket.D client successfully connected: {link={}}", config().getLinkUrl());

        return session;
    }

    /**
     * 创建连接器
     */
    protected abstract ClientConnector createConnector();
}