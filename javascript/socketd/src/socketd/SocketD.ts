import type {Client} from "./transport/client/Client";
import type {ClientProvider} from "./transport/client/ClientProvider";
import {Asserts} from "./transport/core/Asserts";
import {ClientConfig} from "./transport/client/ClientConfig";
import {ClusterClient} from "./cluster/ClusterClient";
import {WsClientProvider} from "../socketd_websocket/WsClientProvider";
import {EntityDefault, FileEntity, StringEntity} from "./transport/core/Entity";
import {EventListener, Listener, PathListener, PipelineListener, SimpleListener} from "./transport/core/Listener";
import type {RouteSelector} from "./transport/core/RouteSelector";
import type {IoBiConsumer} from "./transport/core/Typealias";
import type {Session} from "./transport/core/Session";
import type {Message} from "./transport/core/Message";
import {EntityMetas} from "./transport/core/Constants";


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

//
// 下面是快捷接口（对外需要 new 的接口都在了）
//

/**
 * 框架版本号
 */
export function version():string{
    return SocketD.version();
}

/**
 * 协议版本号
 */
export function protocolVersion():string{
    return SocketD.protocolVersion();
}

/**
 * 创建客户端（支持 url 自动识别）
 *
 * @param serverUrl 服务器地址
 */
export function createClient(serverUrl: string): Client {
    return SocketD.createClient(serverUrl);
}

/**
 * 创建客户端（支持 url 自动识别），如果没有则为 null
 *
 * @param serverUrl 服务器地址
 */
export function createClientOrNull(serverUrl: string): Client | null {
    return SocketD.createClientOrNull(serverUrl);
}

/**
 * 创建集群客户端
 *
 * @param serverUrls 服务端地址
 */
export function createClusterClient(serverUrls: string[]): ClusterClient {
    return SocketD.createClusterClient(serverUrls);
}

/**
 * 创建实体
 * */
export function newEntity():EntityDefault {
    return new EntityDefault();
}

/**
 * 创建字符串实体
 * */
export function newStringEntity(data:string):StringEntity {
    return new StringEntity(data);
}

/**
 * 创建文件实体
 * */
export function newFileEntity(file:File):FileEntity{
    return new FileEntity(file);
}

/**
 * 创建简单临听器
 * */
export function newSimpleListener():SimpleListener {
    return new SimpleListener();
}

/**
 * 创建事件监听器
 * */
export function newEventListener(routeSelector?: RouteSelector<IoBiConsumer<Session, Message>>):EventListener{
    return new EventListener(routeSelector);
}


/**
 * 创建路径监听器（一般用于服务端）
 * */
export function newPathListener(routeSelector?: RouteSelector<Listener>):PathListener{
    return new PathListener(routeSelector);
}

/**
 * 创建管道监听器
 * */
export function newPipelineListener():PipelineListener {
    return new PipelineListener();
}

/**
 * 元信息字典
 * */
export const Metas = EntityMetas;

