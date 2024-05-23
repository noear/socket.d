package org.noear.socketd.transport.client;

import org.noear.socketd.exception.SocketDException;
import org.noear.socketd.transport.client.impl.ClientConnectHandlerDefault;
import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.impl.ProcessorDefault;
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
    protected ClientHeartbeatHandler heartbeatHandler;
    //连接处理
    protected ClientConnectHandler connectHandler = new ClientConnectHandlerDefault();

    //配置
    private final ClientConfig config;
    //助理
    private final T assistant;

    public ClientBase(ClientConfig config, T assistant) {
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
    public ClientConfig getConfig() {
        return config;
    }

    /**
     * 获取处理器
     */
    @Override
    public Processor getProcessor() {
        return processor;
    }

    /**
     * 获取连接处理器
     */
    @Override
    public ClientConnectHandler getConnectHandler() {
        return connectHandler;
    }

    /**
     * 获取心跳处理
     */
    @Override
    public ClientHeartbeatHandler getHeartbeatHandler() {
        return heartbeatHandler;
    }

    /**
     * 获取心跳间隔（毫秒）
     */
    @Override
    public long getHeartbeatInterval() {
        return config.getHeartbeatInterval();
    }


    /**
     * 设置连接处理器
     */
    @Override
    public Client connectHandler(ClientConnectHandler connectHandler) {
        if (connectHandler != null) {
            this.connectHandler = connectHandler;
        }

        return this;
    }

    /**
     * 设置心跳处理器
     */
    @Override
    public Client heartbeatHandler(ClientHeartbeatHandler heartbeatHandler) {
        if (heartbeatHandler != null) {
            this.heartbeatHandler = heartbeatHandler;
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
    public ClientSession open() {
        try {
            return openDo(false);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 打开会话或出异常
     */
    @Override
    public ClientSession openOrThow() throws IOException {
        return openDo(true);
    }

    private Session openDo(boolean isThow) throws IOException {
        ClientConnector connector = createConnector();
        ClientChannel clientChannel = new ClientChannel(this, connector);

        try {
            clientChannel.connect();

            log.info("Socket.D client successfully connected: {link={}}", getConfig().getLinkUrl());
        } catch (Throwable e) {
            if (isThow) {

                clientChannel.close(Constants.CLOSE2008_OPEN_FAIL);

                if (e instanceof RuntimeException || e instanceof IOException) {
                    throw e;
                } else {
                    throw new SocketDException("Socket.D client Connection failed", e);
                }
            } else {
                log.info("Socket.D client Connection failed: {link={}}", getConfig().getLinkUrl());
            }
        }

        return clientChannel.getSession();
    }

    /**
     * 创建连接器
     */
    protected abstract ClientConnector createConnector();
}