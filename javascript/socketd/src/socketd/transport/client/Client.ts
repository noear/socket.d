import {Listener, SimpleListener} from "../core/Listener";
import {IoConsumer} from "../core/Types";
import {ClientSession} from "./ClientSession";
import {ClientConfig} from "./ClientConfig";
import {Processor, ProcessorDefault} from "../core/Processor";
import {ChannelAssistant} from "../core/ChannelAssistant";
import {Session} from "../core/Session";
import {ClientConnector} from "./ClientConnector";
import {ClientChannel} from "./ClientChannel";
import {SessionDefault} from "../core/SessionDefault";

/**
 * 客户端（用于构建会话）
 *
 * @author noear
 * @since 2.0
 */
export interface Client {
    /**
     * 心跳
     */
    heartbeatHandler(handler: IoConsumer<Session>)

    /**
     * 配置
     */
    config(configHandler: IoConsumer<ClientConfig>)

    /**
     * 监听
     */
    listen(listener: Listener): Client;

    /**
     * 打开会话
     */
    open(): Promise<ClientSession>;
}


/**
 * 客户端内部扩展接口
 *
 * @author noear
 * @since  2.1
 */
export interface ClientInternal extends Client {
    /**
     * 获取心跳处理
     */
    getHeartbeatHandler(): IoConsumer<Session>;

    /**
     * 获取心跳间隔（毫秒）
     */
    getHeartbeatInterval(): number;

    /**
     * 获取配置
     */
    getConfig(): ClientConfig;

    /**
     * 获取处理器
     */
    getProcessor(): Processor;
}

/**
 * 客户端基类
 *
 * @author noear
 * @since 2.0
 */
export abstract class ClientBase<T extends ChannelAssistant<Object>> implements ClientInternal {
    private _config: ClientConfig;
    private _heartbeatHandler: IoConsumer<Session>;
    private _processor: Processor;
    private _assistant: T;

    constructor(clientConfig: ClientConfig, assistant: T) {
        this._config = clientConfig;
        this._assistant = assistant;
        this._processor = new ProcessorDefault();
    }

    /**
     * 获取通道助理
     */
    getAssistant(): T {
        return this._assistant;
    }

    /**
     * 获取心跳处理
     */
    getHeartbeatHandler(): IoConsumer<Session> {
        return this._heartbeatHandler;
    }

    /**
     * 获取心跳间隔（毫秒）
     */
    getHeartbeatInterval(): number {
        return this.getConfig().getHeartbeatInterval();
    }

    /**
     * 获取配置
     */
    getConfig(): ClientConfig {
        return this._config;
    }

    /**
     * 获取处理器
     */
    getProcessor(): Processor {
        return this._processor;
    }

    /**
     * 设置心跳
     */
    heartbeatHandler(handler: IoConsumer<Session>) {
        if (handler != null) {
            this._heartbeatHandler = handler;
        }

        return this;
    }

    /**
     * 配置
     */
    config(configHandler: IoConsumer<ClientConfig>) {
        if (configHandler != null) {
            configHandler(this._config);
        }

        return this;
    }

    /**
     * 设置监听器
     */
    listen(listener: Listener): Client {
        if (listener != null) {
            this._processor.setListener(listener);
        }

        return this;
    }

    /**
     * 打开会话
     */
    async open(): Promise<ClientSession> {
        let connector = this.createConnector();

        //连接
        let channel0 = await connector.connect();
        //新建客户端通道
        let clientChannel = new ClientChannel(channel0, connector);
        //同步握手信息
        clientChannel.setHandshake(channel0.getHandshake());
        let session = new SessionDefault(clientChannel);
        //原始通道切换为带壳的 session
        channel0.setSession(session);

        console.info("Socket.D client successfully connected: {link={}}", this.getConfig().getLinkUrl());

        return session;
    }

    /**
     * 创建连接器
     */
    protected abstract createConnector(): ClientConnector;
}
