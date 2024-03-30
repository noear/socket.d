import type {Client} from "./transport/client/Client";
import type {ClientProvider} from "./transport/client/ClientProvider";
import {Asserts} from "./transport/core/Asserts";
import {ClientConfig} from "./transport/client/ClientConfig";
import {ClusterClient} from "./cluster/ClusterClient";
import {WsProvider} from "./transport_websocket/WsProvider";
import {EntityDefault, FileEntity, StringEntity} from "./transport/core/Entity";
import {EventListener, Listener, PathListener, PipelineListener, SimpleListener} from "./transport/core/Listener";
import type {RouteSelector} from "./transport/core/RouteSelector";
import type {IoBiConsumer} from "./transport/core/Typealias";
import type {Session} from "./transport/core/Session";
import type {Message} from "./transport/core/Message";
import {EntityMetas} from "./transport/core/EntityMetas";
import {ServerProvider} from "./transport/server/ServerProvider";
import {ServerConfig} from "./transport/server/ServerConfig";
import {Server} from "./transport/server/Server";
import {BrokerListener} from "./broker/BrokerListener";
import {BrokerFragmentHandler} from "./broker/BrokerFragmentHandler";

export class SocketD {
    /**
     * 元信息字典
     * */
    static EntityMetas = EntityMetas;

    private static clientProviderMap: Map<string, ClientProvider> = new Map<string, ClientProvider>();
    private static serverProviderMap: Map<string, ServerProvider> = new Map<string, ServerProvider>();

    static {
        const provider = new WsProvider();
        for (const s of provider.schemas()) {
            this.clientProviderMap.set(s, provider);
        }

        for (const s of provider.schemas()) {
            this.serverProviderMap.set(s, provider);
        }
    }

    /**
     * 框架版本号
     */
    static version(): string {
        return "2.4.9";
    }

    /**
     * 协议版本号
     */
    static protocolVersion(): string {
        return "1.0";
    }

    /**
     * 创建服务端
     */
    static createServer(schema: string): Server {
        let server = this.createServerOrNull(schema);
        if (server == null) {
            throw new Error("No socketd server providers were found: " + schema);
        } else {
            return server;
        }
    }

    /**
     * 创建服务端，如果没有则为 null
     */
    static createServerOrNull(schema: string): Server | null {
        Asserts.assertNull("schema", schema);

        let factory = SocketD.serverProviderMap.get(schema);
        if (factory == null) {
            return null;
        } else {
            return factory.createServer(new ServerConfig(schema));
        }
    }

    /**
     * 创建客户端（支持 url 自动识别）
     *
     * @param serverUrl 服务器地址
     */
    static createClient(serverUrl: string): Client {
        const client = this.createClientOrNull(serverUrl);
        if (client == null) {
            throw new Error("No socketd client providers were found: " + serverUrl);
        } else {
            return client;
        }
    }

    /**
     * 创建客户端（支持 url 自动识别），如果没有则为 null
     *
     * @param serverUrl 服务器地址
     */
    static createClientOrNull(serverUrl: string): Client | null {
        Asserts.assertNull("serverUrl", serverUrl);

        const idx = serverUrl.indexOf("://");
        if (idx < 2) {
            throw new Error("The serverUrl invalid: " + serverUrl);
        }

        const schema = serverUrl.substring(0, idx);

        const factory = this.clientProviderMap.get(schema);
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
    static createClusterClient(serverUrls: string[] | string): ClusterClient {
        return new ClusterClient(serverUrls);
    }

    /**
     * 创建实体
     * */
    static newEntity(data?: string | Blob | ArrayBuffer): EntityDefault {
        if (!data) {
            return new EntityDefault();
        } else if (data instanceof File) {
            return new FileEntity(data);
        } else if (data instanceof ArrayBuffer) {
            return new EntityDefault().dataSet(data);
        } else if (data instanceof Blob) {
            return new EntityDefault().dataSet(data);
        } else {
            return new StringEntity(data.toString());
        }
    }

    /**
     * 创建简单临听器
     * */
    static newSimpleListener(): SimpleListener {
        return new SimpleListener();
    }

    /**
     * 创建事件监听器
     * */
    static newEventListener(routeSelector?: RouteSelector<IoBiConsumer<Session, Message>>): EventListener {
        return new EventListener(routeSelector);
    }


    /**
     * 创建路径监听器（一般用于服务端）
     * */
    static newPathListener(routeSelector?: RouteSelector<Listener>): PathListener {
        return new PathListener(routeSelector);
    }

    /**
     * 创建管道监听器
     * */
    static newPipelineListener(): PipelineListener {
        return new PipelineListener();
    }

    /**
     * 创建经理人监听器
     * */
    static newBrokerListener(): BrokerListener {
        return new BrokerListener();
    }

    /**
     * 创建经理人分布处理
     * */
    static newBrokerFragmentHandler(): BrokerFragmentHandler {
        return new BrokerFragmentHandler();
    }
}
