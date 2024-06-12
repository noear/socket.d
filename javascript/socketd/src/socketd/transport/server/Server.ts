import {IoConsumer} from "../core/Typealias";
import {ServerConfig} from "./ServerConfig";
import {Listener} from "../core/Listener";

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