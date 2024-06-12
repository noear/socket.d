import type {Listener} from "../core/Listener";
import type {IoConsumer, IoFunction} from "../core/Typealias";
import type {ClientSession} from "./ClientSession";
import type {ClientConfig} from "./ClientConfig";
import {Processor} from "../core/Processor";
import type {Session} from "../core/Session";
import type {ClientConnector} from "./ClientConnector";
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