/*!
 * Socket.D v2.2.2
 * (c) 2023-2024 noear.org and other contributors
 * Released under the Apache-2.0 License.
 */
import { Asserts } from "./transport/core/Asserts";
import { ClientConfig } from "./transport/client/ClientConfig";
import { ClusterClient } from "./cluster/ClusterClient";
import { WsClientProvider } from "./transport_websocket/WsClientProvider";
import { EntityDefault, FileEntity, StringEntity } from "./transport/core/Entity";
import { EventListener, PathListener, PipelineListener, SimpleListener } from "./transport/core/Listener";
import { EntityMetas } from "./transport/core/Constants";
const clientProviderMap = new Map();
//init
(function () {
    const provider = new WsClientProvider();
    for (const s of provider.schemas()) {
        clientProviderMap.set(s, provider);
    }
})();
/**
 * 框架版本号
 */
export function version() {
    return "2.2.2";
}
/**
 * 协议版本号
 */
export function protocolVersion() {
    return "1.0";
}
/**
 * 创建客户端（支持 url 自动识别）
 *
 * @param serverUrl 服务器地址
 */
export function createClient(serverUrl) {
    const client = createClientOrNull(serverUrl);
    if (client == null) {
        throw new Error("No socketd client providers were found.");
    }
    else {
        return client;
    }
}
/**
 * 创建客户端（支持 url 自动识别），如果没有则为 null
 *
 * @param serverUrl 服务器地址
 */
export function createClientOrNull(serverUrl) {
    Asserts.assertNull("serverUrl", serverUrl);
    const idx = serverUrl.indexOf("://");
    if (idx < 2) {
        throw new Error("The serverUrl invalid: " + serverUrl);
    }
    const schema = serverUrl.substring(0, idx);
    const factory = clientProviderMap.get(schema);
    if (factory == null) {
        return null;
    }
    else {
        const clientConfig = new ClientConfig(serverUrl);
        return factory.createClient(clientConfig);
    }
}
/**
 * 创建集群客户端
 *
 * @param serverUrls 服务端地址
 */
export function createClusterClient(serverUrls) {
    return new ClusterClient(serverUrls);
}
/**
 * 创建实体
 * */
export function newEntity(data) {
    if (!data) {
        return new EntityDefault();
    }
    else if (data instanceof File) {
        return new FileEntity(data);
    }
    else if (data instanceof ArrayBuffer) {
        return new EntityDefault().dataSet(data);
    }
    else if (data instanceof Blob) {
        return new EntityDefault().dataSet(data);
    }
    else {
        return new StringEntity(data.toString());
    }
}
/**
 * 创建简单临听器
 * */
export function newSimpleListener() {
    return new SimpleListener();
}
/**
 * 创建事件监听器
 * */
export function newEventListener(routeSelector) {
    return new EventListener(routeSelector);
}
/**
 * 创建路径监听器（一般用于服务端）
 * */
export function newPathListener(routeSelector) {
    return new PathListener(routeSelector);
}
/**
 * 创建管道监听器
 * */
export function newPipelineListener() {
    return new PipelineListener();
}
/**
 * 元信息字典
 * */
export const Metas = EntityMetas;
