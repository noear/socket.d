import {IoConsumer} from "../core/Typealias";
import {ServerConfig} from "./ServerConfig";
import {Listener} from "../core/Listener";
import {ChannelAssistant} from "../core/ChannelAssistant";
import {Processor, ProcessorDefault} from "../core/Processor";

/**
 * 服务端
 *
 * @author noear
 * @since 2.0
 */
export interface Server {
    /**
     * 获取台头
     * */
    getTitle(): string;

    /**
     * 获取配置
     * */
    getConfig(): ServerConfig;

    /**
     * 配置
     */
    config(configHandler: IoConsumer<ServerConfig>): Server;

    /**
     * 监听
     */
    listen(listener: Listener): Server;

    /**
     * 启动
     */
    start(): Server;

    /**
     * 停止
     */
    stop();
}



/**
 * 服务端基类
 *
 * @author noear
 * @since 2.0
 */
export abstract class ServerBase<T extends ChannelAssistant<any>> implements Server {
    private _processor: Processor = new ProcessorDefault();

    private _config: ServerConfig;
    private _assistant: T;
    protected _isStarted: boolean;

    constructor(config: ServerConfig, assistant: T) {
        this._config = config;
        this._assistant = assistant;
    }

    /**
     * 获取通道助理
     */
    getAssistant(): T {
        return this._assistant;
    }

    abstract getTitle(): string ;

    /**
     * 获取配置
     */
    getConfig(): ServerConfig {
        return this._config;
    }

    /**
     * 配置
     */
    config(configHandler: IoConsumer<ServerConfig>): Server {
        if (configHandler) {
            configHandler(this._config);
        }
        return this;
    }


    /**
     * 获取处理器
     */
    getProcessor(): Processor {
        return this._processor;
    }


    /**
     * 设置监听器
     */
    listen(listener: Listener): Server {
        if (listener) {
            this._processor.setListener(listener);
        }
        return this;
    }

    abstract start(): Server;

    abstract stop();
}
