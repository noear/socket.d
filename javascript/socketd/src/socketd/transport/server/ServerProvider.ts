import {ServerConfig} from "./ServerConfig";
import {Server} from "./Server";

/**
 * 服务端工厂
 *
 * @author noear
 * @since 2.0
 */
export interface ServerProvider {
    /**
     * 协议架构
     */
    schemas(): string[];

    /**
     * 创建服务端
     */
    createServer(serverConfig: ServerConfig): Server;
}