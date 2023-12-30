import type {Client} from "./transport/client/Client";
import type {ClientProvider} from "./transport/client/ClientProvider";
import {Asserts} from "./transport/core/Asserts";
import {ClientConfig} from "./transport/client/ClientConfig";
import {ClusterClient} from "./cluster/ClusterClient";
import {WsClientProvider} from "../socketd_websocket/WsClientProvider";


export class SocketD {
    /**
     * 框架版本号
     */
    static version(): string {
        return "2.2.1";
    }

    /**
     * 协议版本号
     */
    static protocolVersion(): string {
        return "1.0";
    }

    static clientProviderMap: Map<String, ClientProvider> = new Map<String, ClientProvider>();

    static {
        const provider = new WsClientProvider();
        for (const s of provider.schemas()) {
            SocketD.clientProviderMap.set(s, provider);
        }
    }


    /**
     * 创建客户端（支持 url 自动识别）
     *
     * @param serverUrl 服务器地址
     */
    static createClient(serverUrl: string): Client {
        const client = SocketD.createClientOrNull(serverUrl);
        if (client == null) {
            throw new Error("No socketd client providers were found.");
        } else {
            return client;
        }
    }

    /**
     * 创建客户端（支持 url 自动识别），如果没有则为 null
     *
     * @param serverUrl 服务器地址
     */
    static createClientOrNull(serverUrl: string): Client | null{
        Asserts.assertNull("serverUrl", serverUrl);

        const idx = serverUrl.indexOf("://");
        if (idx < 2) {
            throw new Error("The serverUrl invalid: " + serverUrl);
        }

        const schema = serverUrl.substring(0, idx);

        const factory = SocketD.clientProviderMap.get(schema);
        if (factory == null) {
            return null;
        } else {
            const clientConfig = new ClientConfig(serverUrl);
            return factory.createClient(clientConfig);
        }
    }

    /**
     * 创建集群客户端
     *
     * @param serverUrls 服务端地址
     */
    static createClusterClient(serverUrls: string[]): ClusterClient {
        return new ClusterClient(serverUrls);
    }
}