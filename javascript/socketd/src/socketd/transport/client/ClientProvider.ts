import type {Client} from "./Client";
import type {ClientConfig} from "./ClientConfig";

/**
 * 客户端工厂
 *
 * @author noear
 * @since 2.0
 */
export interface ClientProvider {
    /**
     * 协议架构
     */
    schemas(): string[];

    /**
     * 创建客户端
     */
    createClient(clientConfig: ClientConfig): Client;
}