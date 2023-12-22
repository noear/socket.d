import {Listener, SimpleListener} from "../core/Listener";
import {IoConsumer} from "../core/Types";
import {ClientSession} from "./ClientSession";
import {ClientConfig} from "./ClientConfig";
import {Processor, ProcessorDefault} from "../core/Processor";
import {HeartbeatHandler} from "../core/HeartbeatHandler";
import {ChannelAssistant} from "../core/ChannelAssistant";

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
    heartbeatHandler(handler: HeartbeatHandler)

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
    open(): ClientSession;
}

export interface ClientInternal extends Client {
    /**
     * 获取心跳处理
     */
    getHeartbeatHandler(): HeartbeatHandler;

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
export abstract class ClientBase<T extends ChannelAssistant<T>> implements ClientInternal {
    _config: ClientConfig;
    _heartbeatHandler: HeartbeatHandler;
    _processor: Processor;
    _listener: Listener;
    _assistant: T;

    constructor(clientConfig: ClientConfig, assistant: T) {
        this._config = clientConfig;
        this._assistant = assistant;
        this._processor = new ProcessorDefault();
        this._listener = new SimpleListener();
    }

    getAssistant(): T {
        return this._assistant;
    }

    getHeartbeatHandler(): HeartbeatHandler {
        return this._heartbeatHandler;
    }

    getHeartbeatInterval(): number {
        return this.getConfig().getHeartbeatInterval();
    }

    getConfig(): ClientConfig {
        return this._config;
    }

    getProcessor(): Processor {
        return this._processor;
    }

    heartbeatHandler(handler: HeartbeatHandler) {
        if (handler != null) {
            this._heartbeatHandler = handler;
        }

        return this;
    }

    config(configHandler: IoConsumer<ClientConfig>) {
        if (configHandler != null) {
            configHandler(this._config);
        }

        return this;
    }

    listen(listener: Listener): Client {
        if (listener != null) {
            this._listener = listener;
        }

        return this;
    }

    open(): ClientSession {
        throw new Error("Method not implemented.");
    }
}
