"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.Metas = exports.newPipelineListener = exports.newPathListener = exports.newEventListener = exports.newSimpleListener = exports.newEntity = exports.createClusterClient = exports.createClientOrNull = exports.createClient = exports.protocolVersion = exports.version = void 0;
const Asserts_1 = require("./transport/core/Asserts");
const ClientConfig_1 = require("./transport/client/ClientConfig");
const ClusterClient_1 = require("./cluster/ClusterClient");
const WsClientProvider_1 = require("./transport_websocket/WsClientProvider");
const Entity_1 = require("./transport/core/Entity");
const Listener_1 = require("./transport/core/Listener");
const Constants_1 = require("./transport/core/Constants");
const clientProviderMap = new Map();
//init
(function () {
    const provider = new WsClientProvider_1.WsClientProvider();
    for (const s of provider.schemas()) {
        clientProviderMap.set(s, provider);
    }
})();
/**
 * 框架版本号
 */
function version() {
    return "2.2.2";
}
exports.version = version;
/**
 * 协议版本号
 */
function protocolVersion() {
    return "1.0";
}
exports.protocolVersion = protocolVersion;
/**
 * 创建客户端（支持 url 自动识别）
 *
 * @param serverUrl 服务器地址
 */
function createClient(serverUrl) {
    const client = createClientOrNull(serverUrl);
    if (client == null) {
        throw new Error("No socketd client providers were found.");
    }
    else {
        return client;
    }
}
exports.createClient = createClient;
/**
 * 创建客户端（支持 url 自动识别），如果没有则为 null
 *
 * @param serverUrl 服务器地址
 */
function createClientOrNull(serverUrl) {
    Asserts_1.Asserts.assertNull("serverUrl", serverUrl);
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
        const clientConfig = new ClientConfig_1.ClientConfig(serverUrl);
        return factory.createClient(clientConfig);
    }
}
exports.createClientOrNull = createClientOrNull;
/**
 * 创建集群客户端
 *
 * @param serverUrls 服务端地址
 */
function createClusterClient(serverUrls) {
    return new ClusterClient_1.ClusterClient(serverUrls);
}
exports.createClusterClient = createClusterClient;
/**
 * 创建实体
 * */
function newEntity(data) {
    if (!data) {
        return new Entity_1.EntityDefault();
    }
    else if (data instanceof File) {
        return new Entity_1.FileEntity(data);
    }
    else if (data instanceof ArrayBuffer) {
        return new Entity_1.EntityDefault().dataSet(data);
    }
    else if (data instanceof Blob) {
        return new Entity_1.EntityDefault().dataSet(data);
    }
    else {
        return new Entity_1.StringEntity(data.toString());
    }
}
exports.newEntity = newEntity;
/**
 * 创建简单临听器
 * */
function newSimpleListener() {
    return new Listener_1.SimpleListener();
}
exports.newSimpleListener = newSimpleListener;
/**
 * 创建事件监听器
 * */
function newEventListener(routeSelector) {
    return new Listener_1.EventListener(routeSelector);
}
exports.newEventListener = newEventListener;
/**
 * 创建路径监听器（一般用于服务端）
 * */
function newPathListener(routeSelector) {
    return new Listener_1.PathListener(routeSelector);
}
exports.newPathListener = newPathListener;
/**
 * 创建管道监听器
 * */
function newPipelineListener() {
    return new Listener_1.PipelineListener();
}
exports.newPipelineListener = newPipelineListener;
/**
 * 元信息字典
 * */
exports.Metas = Constants_1.EntityMetas;
