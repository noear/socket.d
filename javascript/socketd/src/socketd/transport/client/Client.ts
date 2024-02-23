import type {Listener} from "../core/Listener";
import type {IoConsumer, IoFunction} from "../core/Typealias";
import type {ClientSession} from "./ClientSession";
import type {ClientConfig} from "./ClientConfig";
import {Processor, ProcessorDefault} from "../core/Processor";
import type {ChannelAssistant} from "../core/ChannelAssistant";
import type {Session} from "../core/Session";
import type {ClientConnector} from "./ClientConnector";
import {ClientChannel} from "./ClientChannel";
import {Constants} from "../core/Constants";
import {ChannelInternal} from "../core/Channel";

/**
 * 客户端（用于构建会话）
 *
 * @author noear
 * @since 2.0
 */
export interface Client {
    /**
     * 连接处理
     */
    connectHandler(handler: IoFunction<ClientConnector, Promise<ChannelInternal>>);

    /**
     * 心跳处理
     */
    heartbeatHandler(handler: IoConsumer<Session>)

    /**
     * 配置处理
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

    /**
     * 打开会话或出异常（即要求第一次是连接成功的）
     */
    openOrThow(): Promise<ClientSession>;
}


/**
 * 客户端内部扩展接口
 *
 * @author noear
 * @since  2.1
 */
export interface ClientInternal extends Client {
    /**
     * 获取连接处理器
     */
    getConnectHandler(): IoFunction<ClientConnector, Promise<ChannelInternal>>;

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
    private _connectHandler : IoFunction<ClientConnector, Promise<ChannelInternal>>;
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

    getConnectHandler(): IoFunction<ClientConnector, Promise<ChannelInternal>> {
        return this._connectHandler;
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
     * 设置连接处理器
     */
    connectHandler(handler: IoFunction<ClientConnector, Promise<ChannelInternal>>) {
        if (handler != null) {
            this._connectHandler = handler;
        }

        return this;
    }

    /**
     * 设置心跳处理器
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


    open(): Promise<ClientSession> {
        return this.openDo(false);
    }

    /**
     * 打开会话
     */
    openOrThow(): Promise<ClientSession> {
        return this.openDo(true);
    }

    private openDo(isThow: boolean): Promise<ClientSession> {
        const connector = this.createConnector();
        const clientChannel = new ClientChannel(this, connector);

        return new Promise<ClientSession>((resolve, reject) => {
            // @ts-ignore
            clientChannel.connect().then(res => {
                console.info("Socket.D client successfully connected!");
                resolve(clientChannel.getSession());
            }, err => {
                if (isThow) {
                    clientChannel.close(Constants.CLOSE2008_OPEN_FAIL);
                    reject(err);
                } else {
                    console.warn("Socket.D client Connection failed!");
                    resolve(clientChannel.getSession());
                }
            })
        });
    }

    /**
     * 创建连接器
     */
    protected abstract createConnector(): ClientConnector;
}
