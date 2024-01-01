/******/ (() => { // webpackBootstrap
/******/ 	"use strict";
/******/ 	var __webpack_modules__ = ({

/***/ "./src/socketd/cluster/ClusterClient.ts":
/*!**********************************************!*\
  !*** ./src/socketd/cluster/ClusterClient.ts ***!
  \**********************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   ClusterClient: () => (/* binding */ ClusterClient)
/* harmony export */ });
/* harmony import */ var _ClusterClientSession__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./ClusterClientSession */ "./src/socketd/cluster/ClusterClientSession.ts");
/* harmony import */ var _socketd__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../socketd */ "./src/socketd/socketd.ts");
var __awaiter = (undefined && undefined.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};


/**
 * 集群客户端
 *
 * @author noear
 */
class ClusterClient {
    constructor(serverUrls) {
        if (serverUrls instanceof Array) {
            this._serverUrls = serverUrls;
        }
        else {
            this._serverUrls = [serverUrls];
        }
    }
    heartbeatHandler(heartbeatHandler) {
        this._heartbeatHandler = heartbeatHandler;
        return this;
    }
    /**
     * 配置
     */
    config(configHandler) {
        this._configHandler = configHandler;
        return this;
    }
    /**
     * 监听
     */
    listen(listener) {
        this._listener = listener;
        return this;
    }
    /**
     * 打开
     */
    open() {
        return __awaiter(this, void 0, void 0, function* () {
            const sessionList = new Array();
            for (const urls of this._serverUrls) {
                for (let url of urls.split(",")) {
                    url = url.trim();
                    if (!url) {
                        continue;
                    }
                    const client = (0,_socketd__WEBPACK_IMPORTED_MODULE_1__.createClient)(url);
                    if (this._listener != null) {
                        client.listen(this._listener);
                    }
                    if (this._configHandler != null) {
                        client.config(this._configHandler);
                    }
                    if (this._heartbeatHandler != null) {
                        client.heartbeatHandler(this._heartbeatHandler);
                    }
                    const session = yield client.open();
                    sessionList.push(session);
                }
            }
            return new _ClusterClientSession__WEBPACK_IMPORTED_MODULE_0__.ClusterClientSession(sessionList);
        });
    }
}


/***/ }),

/***/ "./src/socketd/cluster/ClusterClientSession.ts":
/*!*****************************************************!*\
  !*** ./src/socketd/cluster/ClusterClientSession.ts ***!
  \*****************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   ClusterClientSession: () => (/* binding */ ClusterClientSession)
/* harmony export */ });
/* harmony import */ var _utils_StrUtils__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../utils/StrUtils */ "./src/socketd/utils/StrUtils.ts");
/* harmony import */ var _exception_SocketdException__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../exception/SocketdException */ "./src/socketd/exception/SocketdException.ts");
/* harmony import */ var _utils_RunUtils__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../utils/RunUtils */ "./src/socketd/utils/RunUtils.ts");



/**
 * 集群客户端会话
 *
 * @author noear
 * @since 2.1
 */
class ClusterClientSession {
    constructor(sessions) {
        this._sessionSet = sessions;
        this._sessionId = _utils_StrUtils__WEBPACK_IMPORTED_MODULE_0__.StrUtils.guid();
        this._sessionRoundCounter = 0;
    }
    /**
     * 获取所有会话
     */
    getSessionAll() {
        return this._sessionSet;
    }
    /**
     * 获取一个会话（轮询负栽均衡）
     */
    getSessionOne() {
        if (this._sessionSet.length == 0) {
            //没有会话
            throw new _exception_SocketdException__WEBPACK_IMPORTED_MODULE_1__.SocketdException("No session!");
        }
        else if (this._sessionSet.length == 1) {
            //只有一个就不管了
            return this._sessionSet[0];
        }
        else {
            //查找可用的会话
            const sessions = new Array();
            for (const s of this._sessionSet) {
                if (s.isValid()) {
                    sessions.push(s);
                }
            }
            if (sessions.length == 0) {
                //没有可用的会话
                throw new _exception_SocketdException__WEBPACK_IMPORTED_MODULE_1__.SocketdException("No session is available!");
            }
            if (sessions.length == 1) {
                return sessions[0];
            }
            //论询处理
            const counter = this._sessionRoundCounter++;
            const idx = counter % sessions.length;
            if (counter > 999999999) {
                this._sessionRoundCounter = 0;
            }
            return sessions[idx];
        }
    }
    isValid() {
        for (const session of this._sessionSet) {
            if (session.isValid()) {
                return true;
            }
        }
        return false;
    }
    sessionId() {
        return this._sessionId;
    }
    reconnect() {
        for (const session of this._sessionSet) {
            if (session.isValid() == false) {
                session.reconnect();
            }
        }
    }
    /**
     * 发送
     *
     * @param event   事件
     * @param content 内容
     */
    send(event, content) {
        const sender = this.getSessionOne();
        sender.send(event, content);
    }
    /**
     * 发送并请求（限为一次答复；指定回调）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     * @param timeout  超时
     */
    sendAndRequest(event, content, consumer, timeout) {
        const sender = this.getSessionOne();
        return sender.sendAndRequest(event, content, consumer, timeout);
    }
    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     * @param timeout  超时
     */
    sendAndSubscribe(event, content, consumer, timeout) {
        const sender = this.getSessionOne();
        return sender.sendAndSubscribe(event, content, consumer, timeout);
    }
    /**
     * 关闭
     */
    close() {
        for (const session of this._sessionSet) {
            //某个关闭出错，不影响别的关闭
            _utils_RunUtils__WEBPACK_IMPORTED_MODULE_2__.RunUtils.runAndTry(session.close);
        }
    }
}


/***/ }),

/***/ "./src/socketd/exception/SocketdException.ts":
/*!***************************************************!*\
  !*** ./src/socketd/exception/SocketdException.ts ***!
  \***************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   SocketdAlarmException: () => (/* binding */ SocketdAlarmException),
/* harmony export */   SocketdChannelException: () => (/* binding */ SocketdChannelException),
/* harmony export */   SocketdCodecException: () => (/* binding */ SocketdCodecException),
/* harmony export */   SocketdConnectionException: () => (/* binding */ SocketdConnectionException),
/* harmony export */   SocketdException: () => (/* binding */ SocketdException),
/* harmony export */   SocketdSizeLimitException: () => (/* binding */ SocketdSizeLimitException),
/* harmony export */   SocketdTimeoutException: () => (/* binding */ SocketdTimeoutException)
/* harmony export */ });
/**
 * 异常
 *
 * @author noear
 * @since 2.0
 */
class SocketdException extends Error {
    constructor(message) {
        super(message);
    }
}
/**
 * 告警异常
 *
 * @author noear
 * @since 2.0
 */
class SocketdAlarmException extends SocketdException {
    constructor(from) {
        super(from.entity().dataAsString());
        this._from = from;
    }
    getFrom() {
        return this._from;
    }
}
/**
 * 通道异常
 *
 * @author noear
 * @since 2.0
 */
class SocketdChannelException extends SocketdException {
    constructor(message) {
        super(message);
    }
}
/**
 * 编码异常
 *
 * @author noear
 * @since 2.0
 */
class SocketdCodecException extends SocketdException {
    constructor(message) {
        super(message);
    }
}
/**
 * 连接异常
 *
 * @author noear
 * @since 2.0
 */
class SocketdConnectionException extends SocketdException {
    constructor(message) {
        super(message);
    }
}
/**
 * 大小限制异常
 *
 * @author noear
 * @since 2.0
 */
class SocketdSizeLimitException extends SocketdException {
    constructor(message) {
        super(message);
    }
}
/**
 * 超时异常
 *
 * @author noear
 * @since 2.0
 */
class SocketdTimeoutException extends SocketdException {
    constructor(message) {
        super(message);
    }
}


/***/ }),

/***/ "./src/socketd/socketd.ts":
/*!********************************!*\
  !*** ./src/socketd/socketd.ts ***!
  \********************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   Metas: () => (/* binding */ Metas),
/* harmony export */   createClient: () => (/* binding */ createClient),
/* harmony export */   createClientOrNull: () => (/* binding */ createClientOrNull),
/* harmony export */   createClusterClient: () => (/* binding */ createClusterClient),
/* harmony export */   newEntity: () => (/* binding */ newEntity),
/* harmony export */   newEventListener: () => (/* binding */ newEventListener),
/* harmony export */   newPathListener: () => (/* binding */ newPathListener),
/* harmony export */   newPipelineListener: () => (/* binding */ newPipelineListener),
/* harmony export */   newSimpleListener: () => (/* binding */ newSimpleListener),
/* harmony export */   protocolVersion: () => (/* binding */ protocolVersion),
/* harmony export */   version: () => (/* binding */ version)
/* harmony export */ });
/* harmony import */ var _transport_core_Asserts__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./transport/core/Asserts */ "./src/socketd/transport/core/Asserts.ts");
/* harmony import */ var _transport_client_ClientConfig__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./transport/client/ClientConfig */ "./src/socketd/transport/client/ClientConfig.ts");
/* harmony import */ var _cluster_ClusterClient__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./cluster/ClusterClient */ "./src/socketd/cluster/ClusterClient.ts");
/* harmony import */ var _transport_websocket_WsClientProvider__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./transport_websocket/WsClientProvider */ "./src/socketd/transport_websocket/WsClientProvider.ts");
/* harmony import */ var _transport_core_Entity__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./transport/core/Entity */ "./src/socketd/transport/core/Entity.ts");
/* harmony import */ var _transport_core_Listener__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./transport/core/Listener */ "./src/socketd/transport/core/Listener.ts");
/* harmony import */ var _transport_core_Constants__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./transport/core/Constants */ "./src/socketd/transport/core/Constants.ts");







const clientProviderMap = new Map();
//init
(function () {
    const provider = new _transport_websocket_WsClientProvider__WEBPACK_IMPORTED_MODULE_3__.WsClientProvider();
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
/**
 * 协议版本号
 */
function protocolVersion() {
    return "1.0";
}
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
/**
 * 创建客户端（支持 url 自动识别），如果没有则为 null
 *
 * @param serverUrl 服务器地址
 */
function createClientOrNull(serverUrl) {
    _transport_core_Asserts__WEBPACK_IMPORTED_MODULE_0__.Asserts.assertNull("serverUrl", serverUrl);
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
        const clientConfig = new _transport_client_ClientConfig__WEBPACK_IMPORTED_MODULE_1__.ClientConfig(serverUrl);
        return factory.createClient(clientConfig);
    }
}
/**
 * 创建集群客户端
 *
 * @param serverUrls 服务端地址
 */
function createClusterClient(serverUrls) {
    return new _cluster_ClusterClient__WEBPACK_IMPORTED_MODULE_2__.ClusterClient(serverUrls);
}
/**
 * 创建实体
 * */
function newEntity(data) {
    if (!data) {
        return new _transport_core_Entity__WEBPACK_IMPORTED_MODULE_4__.EntityDefault();
    }
    else if (data instanceof File) {
        return new _transport_core_Entity__WEBPACK_IMPORTED_MODULE_4__.FileEntity(data);
    }
    else if (data instanceof ArrayBuffer) {
        return new _transport_core_Entity__WEBPACK_IMPORTED_MODULE_4__.EntityDefault().dataSet(data);
    }
    else if (data instanceof Blob) {
        return new _transport_core_Entity__WEBPACK_IMPORTED_MODULE_4__.EntityDefault().dataSet(data);
    }
    else {
        return new _transport_core_Entity__WEBPACK_IMPORTED_MODULE_4__.StringEntity(data.toString());
    }
}
/**
 * 创建简单临听器
 * */
function newSimpleListener() {
    return new _transport_core_Listener__WEBPACK_IMPORTED_MODULE_5__.SimpleListener();
}
/**
 * 创建事件监听器
 * */
function newEventListener(routeSelector) {
    return new _transport_core_Listener__WEBPACK_IMPORTED_MODULE_5__.EventListener(routeSelector);
}
/**
 * 创建路径监听器（一般用于服务端）
 * */
function newPathListener(routeSelector) {
    return new _transport_core_Listener__WEBPACK_IMPORTED_MODULE_5__.PathListener(routeSelector);
}
/**
 * 创建管道监听器
 * */
function newPipelineListener() {
    return new _transport_core_Listener__WEBPACK_IMPORTED_MODULE_5__.PipelineListener();
}
/**
 * 元信息字典
 * */
const Metas = _transport_core_Constants__WEBPACK_IMPORTED_MODULE_6__.EntityMetas;


/***/ }),

/***/ "./src/socketd/transport/client/Client.ts":
/*!************************************************!*\
  !*** ./src/socketd/transport/client/Client.ts ***!
  \************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   ClientBase: () => (/* binding */ ClientBase)
/* harmony export */ });
/* harmony import */ var _core_Processor__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../core/Processor */ "./src/socketd/transport/core/Processor.ts");
/* harmony import */ var _ClientChannel__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./ClientChannel */ "./src/socketd/transport/client/ClientChannel.ts");
/* harmony import */ var _core_SessionDefault__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../core/SessionDefault */ "./src/socketd/transport/core/SessionDefault.ts");
var __awaiter = (undefined && undefined.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};



/**
 * 客户端基类
 *
 * @author noear
 * @since 2.0
 */
class ClientBase {
    constructor(clientConfig, assistant) {
        this._config = clientConfig;
        this._assistant = assistant;
        this._processor = new _core_Processor__WEBPACK_IMPORTED_MODULE_0__.ProcessorDefault();
    }
    /**
     * 获取通道助理
     */
    getAssistant() {
        return this._assistant;
    }
    /**
     * 获取心跳处理
     */
    getHeartbeatHandler() {
        return this._heartbeatHandler;
    }
    /**
     * 获取心跳间隔（毫秒）
     */
    getHeartbeatInterval() {
        return this.getConfig().getHeartbeatInterval();
    }
    /**
     * 获取配置
     */
    getConfig() {
        return this._config;
    }
    /**
     * 获取处理器
     */
    getProcessor() {
        return this._processor;
    }
    /**
     * 设置心跳
     */
    heartbeatHandler(handler) {
        if (handler != null) {
            this._heartbeatHandler = handler;
        }
        return this;
    }
    /**
     * 配置
     */
    config(configHandler) {
        if (configHandler != null) {
            configHandler(this._config);
        }
        return this;
    }
    /**
     * 设置监听器
     */
    listen(listener) {
        if (listener != null) {
            this._processor.setListener(listener);
        }
        return this;
    }
    /**
     * 打开会话
     */
    open() {
        return __awaiter(this, void 0, void 0, function* () {
            const connector = this.createConnector();
            //连接
            const channel0 = yield connector.connect();
            //新建客户端通道
            const clientChannel = new _ClientChannel__WEBPACK_IMPORTED_MODULE_1__.ClientChannel(channel0, connector);
            //同步握手信息
            clientChannel.setHandshake(channel0.getHandshake());
            const session = new _core_SessionDefault__WEBPACK_IMPORTED_MODULE_2__.SessionDefault(clientChannel);
            //原始通道切换为带壳的 session
            channel0.setSession(session);
            //console.info(`Socket.D client successfully connected: {link=${this.getConfig().getLinkUrl()}`);
            console.info(`Socket.D client successfully connected!`);
            return session;
        });
    }
}


/***/ }),

/***/ "./src/socketd/transport/client/ClientChannel.ts":
/*!*******************************************************!*\
  !*** ./src/socketd/transport/client/ClientChannel.ts ***!
  \*******************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   ClientChannel: () => (/* binding */ ClientChannel)
/* harmony export */ });
/* harmony import */ var _core_Channel__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../core/Channel */ "./src/socketd/transport/core/Channel.ts");
/* harmony import */ var _core_HeartbeatHandler__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../core/HeartbeatHandler */ "./src/socketd/transport/core/HeartbeatHandler.ts");
/* harmony import */ var _core_Constants__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../core/Constants */ "./src/socketd/transport/core/Constants.ts");
/* harmony import */ var _core_Asserts__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../core/Asserts */ "./src/socketd/transport/core/Asserts.ts");
/* harmony import */ var _exception_SocketdException__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../exception/SocketdException */ "./src/socketd/exception/SocketdException.ts");
/* harmony import */ var _utils_RunUtils__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../utils/RunUtils */ "./src/socketd/utils/RunUtils.ts");
var __awaiter = (undefined && undefined.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};






/**
 * 客户端通道
 *
 * @author noear
 * @since 2.0
 */
class ClientChannel extends _core_Channel__WEBPACK_IMPORTED_MODULE_0__.ChannelBase {
    constructor(real, connector) {
        super(real.getConfig());
        this._connector = connector;
        this._real = real;
        if (connector.heartbeatHandler() == null) {
            this._heartbeatHandler = new _core_HeartbeatHandler__WEBPACK_IMPORTED_MODULE_1__.HeartbeatHandlerDefault(null);
        }
        else {
            this._heartbeatHandler = new _core_HeartbeatHandler__WEBPACK_IMPORTED_MODULE_1__.HeartbeatHandlerDefault(connector.heartbeatHandler());
        }
        this.initHeartbeat();
    }
    /**
     * 初始化心跳（关闭后，手动重链时也会用到）
     */
    initHeartbeat() {
        if (this._heartbeatScheduledFuture) {
            clearInterval(this._heartbeatScheduledFuture);
        }
        if (this._connector.autoReconnect()) {
            this._heartbeatScheduledFuture = setInterval(() => {
                try {
                    this.heartbeatHandle();
                }
                catch (e) {
                    console.warn("Client channel heartbeat error", e);
                }
            }, this._connector.heartbeatInterval());
        }
    }
    /**
     * 心跳处理
     */
    heartbeatHandle() {
        return __awaiter(this, void 0, void 0, function* () {
            if (this._real != null) {
                //说明握手未成
                if (this._real.getHandshake() == null) {
                    return;
                }
                //手动关闭
                if (this._real.isClosed() == _core_Constants__WEBPACK_IMPORTED_MODULE_2__.Constants.CLOSE4_USER) {
                    console.debug(`Client channel is closed (pause heartbeat), sessionId=${this.getSession().sessionId()}`);
                    return;
                }
            }
            try {
                yield this.prepareCheck();
                this._heartbeatHandler.heartbeat(this.getSession());
            }
            catch (e) {
                if (e instanceof _exception_SocketdException__WEBPACK_IMPORTED_MODULE_4__.SocketdException) {
                    throw e;
                }
                if (this._connector.autoReconnect()) {
                    this._real.close(_core_Constants__WEBPACK_IMPORTED_MODULE_2__.Constants.CLOSE3_ERROR);
                    this._real = null;
                }
                throw new _exception_SocketdException__WEBPACK_IMPORTED_MODULE_4__.SocketdChannelException(e);
            }
        });
    }
    /**
     * 预备检测
     *
     * @return 是否为新链接
     */
    prepareCheck() {
        return __awaiter(this, void 0, void 0, function* () {
            if (this._real == null || this._real.isValid() == false) {
                this._real = yield this._connector.connect();
                return true;
            }
            else {
                return false;
            }
        });
    }
    /**
     * 是否有效
     */
    isValid() {
        if (this._real == null) {
            return false;
        }
        else {
            return this._real.isValid();
        }
    }
    /**
     * 是否已关闭
     */
    isClosed() {
        if (this._real == null) {
            return 0;
        }
        else {
            return this._real.isClosed();
        }
    }
    /**
     * 发送
     *
     * @param frame  帧
     * @param stream 流（没有则为 null）
     */
    send(frame, stream) {
        return __awaiter(this, void 0, void 0, function* () {
            _core_Asserts__WEBPACK_IMPORTED_MODULE_3__.Asserts.assertClosedByUser(this._real);
            try {
                yield this.prepareCheck();
                this._real.send(frame, stream);
            }
            catch (e) {
                if (this._connector.autoReconnect()) {
                    this._real.close(_core_Constants__WEBPACK_IMPORTED_MODULE_2__.Constants.CLOSE3_ERROR);
                    this._real = null;
                }
                throw e;
            }
        });
    }
    retrieve(frame) {
        this._real.retrieve(frame);
    }
    reconnect() {
        this.initHeartbeat();
        this.prepareCheck();
    }
    onError(error) {
        this._real.onError(error);
    }
    close(code) {
        _utils_RunUtils__WEBPACK_IMPORTED_MODULE_5__.RunUtils.runAndTry(() => clearInterval(this._heartbeatScheduledFuture));
        _utils_RunUtils__WEBPACK_IMPORTED_MODULE_5__.RunUtils.runAndTry(() => this._connector.close());
        if (this._real) {
            _utils_RunUtils__WEBPACK_IMPORTED_MODULE_5__.RunUtils.runAndTry(() => this._real.close(code));
        }
        super.close(code);
    }
    getSession() {
        return this._real.getSession();
    }
}


/***/ }),

/***/ "./src/socketd/transport/client/ClientConfig.ts":
/*!******************************************************!*\
  !*** ./src/socketd/transport/client/ClientConfig.ts ***!
  \******************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   ClientConfig: () => (/* binding */ ClientConfig)
/* harmony export */ });
/* harmony import */ var _core_Config__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../core/Config */ "./src/socketd/transport/core/Config.ts");

class ClientConfig extends _core_Config__WEBPACK_IMPORTED_MODULE_0__.ConfigBase {
    constructor(url) {
        super(true);
        //支持 sd: 开头的架构
        if (url.startsWith("sd:")) {
            url = url.substring(3);
        }
        this._url = url;
        this._uri = new URL(url);
        this._port = parseInt(this._uri.port);
        this._schema = this._uri.protocol;
        this._linkUrl = "sd:" + url;
        if (this._port < 0) {
            this._port = 8602;
        }
        this._connectTimeout = 10000;
        this._heartbeatInterval = 20000;
        this._autoReconnect = true;
    }
    /**
     * 获取通讯架构（tcp, ws, udp）
     */
    getSchema() {
        return this._schema;
    }
    /**
     * 获取连接地址
     */
    getUrl() {
        return this._url;
    }
    /**
     * 获取连接地址
     */
    getUri() {
        return this._uri;
    }
    /**
     * 获取链接地址
     */
    getLinkUrl() {
        return this._linkUrl;
    }
    /**
     * 获取连接主机
     */
    getHost() {
        return this._uri.host;
    }
    /**
     * 获取连接端口
     */
    getPort() {
        return this._port;
    }
    /**
     * 获取心跳间隔（单位毫秒）
     */
    getHeartbeatInterval() {
        return this._heartbeatInterval;
    }
    /**
     * 配置心跳间隔（单位毫秒）
     */
    heartbeatInterval(heartbeatInterval) {
        this._heartbeatInterval = heartbeatInterval;
        return this;
    }
    /**
     * 获取连接超时（单位毫秒）
     */
    getConnectTimeout() {
        return this._connectTimeout;
    }
    /**
     * 配置连接超时（单位毫秒）
     */
    connectTimeout(connectTimeout) {
        this._connectTimeout = connectTimeout;
        return this;
    }
    /**
     * 获取是否自动重链
     */
    isAutoReconnect() {
        return this._autoReconnect;
    }
    /**
     * 配置是否自动重链
     */
    autoReconnect(autoReconnect) {
        this._autoReconnect = autoReconnect;
        return this;
    }
    idleTimeout(idleTimeout) {
        if (this._autoReconnect == false) {
            //自动重链下，禁用 idleTimeout
            this._idleTimeout = (idleTimeout);
            return this;
        }
        else {
            this._idleTimeout = (0);
            return this;
        }
    }
    toString() {
        return "ClientConfig{" +
            "schema='" + this._schema + '\'' +
            ", charset=" + this._charset +
            ", url='" + this._url + '\'' +
            ", heartbeatInterval=" + this._heartbeatInterval +
            ", connectTimeout=" + this._connectTimeout +
            ", idleTimeout=" + this._idleTimeout +
            ", requestTimeout=" + this._requestTimeout +
            ", readBufferSize=" + this._readBufferSize +
            ", writeBufferSize=" + this._writeBufferSize +
            ", autoReconnect=" + this._autoReconnect +
            ", maxUdpSize=" + this._maxUdpSize +
            '}';
    }
}


/***/ }),

/***/ "./src/socketd/transport/client/ClientConnector.ts":
/*!*********************************************************!*\
  !*** ./src/socketd/transport/client/ClientConnector.ts ***!
  \*********************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   ClientConnectorBase: () => (/* binding */ ClientConnectorBase)
/* harmony export */ });
/**
 * 客户端连接器基类
 *
 * @author noear
 * @since 2.0
 */
class ClientConnectorBase {
    constructor(client) {
        this._client = client;
    }
    heartbeatHandler() {
        return this._client.getHeartbeatHandler();
    }
    heartbeatInterval() {
        return this._client.getHeartbeatInterval();
    }
    autoReconnect() {
        return this._client.getConfig().isAutoReconnect();
    }
}


/***/ }),

/***/ "./src/socketd/transport/client/ClientHandshakeResult.ts":
/*!***************************************************************!*\
  !*** ./src/socketd/transport/client/ClientHandshakeResult.ts ***!
  \***************************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   ClientHandshakeResult: () => (/* binding */ ClientHandshakeResult)
/* harmony export */ });
/**
 * 客户端握手结果
 *
 * @author noear
 * @since 2.0
 */
class ClientHandshakeResult {
    constructor(channel, throwable) {
        this._channel = channel;
        this._throwable = throwable;
    }
    getChannel() {
        return this._channel;
    }
    getThrowable() {
        return this._throwable;
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/Asserts.ts":
/*!***********************************************!*\
  !*** ./src/socketd/transport/core/Asserts.ts ***!
  \***********************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   Asserts: () => (/* binding */ Asserts)
/* harmony export */ });
/* harmony import */ var _Constants__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./Constants */ "./src/socketd/transport/core/Constants.ts");
/* harmony import */ var _exception_SocketdException__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../exception/SocketdException */ "./src/socketd/exception/SocketdException.ts");


/**
 * 断言
 *
 * @author noear
 * @since 2.0
 */
class Asserts {
    /**
     * 断言关闭
     */
    static assertClosed(channel) {
        if (channel != null && channel.isClosed() > 0) {
            throw new _exception_SocketdException__WEBPACK_IMPORTED_MODULE_1__.SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
        }
    }
    /**
     * 断言关闭
     */
    static assertClosedByUser(channel) {
        if (channel != null && channel.isClosed() == _Constants__WEBPACK_IMPORTED_MODULE_0__.Constants.CLOSE4_USER) {
            throw new _exception_SocketdException__WEBPACK_IMPORTED_MODULE_1__.SocketdChannelException("This channel is closed, sessionId=" + channel.getSession().sessionId());
        }
    }
    /**
     * 断言 null
     */
    static assertNull(name, val) {
        if (val == null) {
            throw new Error("The argument cannot be null: " + name);
        }
    }
    /**
     * 断言 empty
     */
    static assertEmpty(name, val) {
        if (!val) {
            throw new Error("The argument cannot be empty: " + name);
        }
    }
    /**
     * 断言 size
     */
    static assertSize(name, size, limitSize) {
        if (size > limitSize) {
            const message = `This message ${name} size is out of limit ${limitSize} (${size})`;
            throw new _exception_SocketdException__WEBPACK_IMPORTED_MODULE_1__.SocketdSizeLimitException(message);
        }
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/Buffer.ts":
/*!**********************************************!*\
  !*** ./src/socketd/transport/core/Buffer.ts ***!
  \**********************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   BlobBuffer: () => (/* binding */ BlobBuffer),
/* harmony export */   ByteBuffer: () => (/* binding */ ByteBuffer)
/* harmony export */ });
class ByteBuffer {
    constructor(buf) {
        this._bufIdx = 0;
        this._buf = buf;
    }
    remaining() {
        return this.size() - this.position();
    }
    position() {
        return this._bufIdx;
    }
    size() {
        return this._buf.byteLength;
    }
    reset() {
        this._bufIdx = 0;
    }
    getBytes(length, callback) {
        let tmpSize = this.remaining();
        if (tmpSize > length) {
            tmpSize = length;
        }
        if (tmpSize <= 0) {
            return false;
        }
        let tmpEnd = this._bufIdx + tmpSize;
        let tmp = this._buf.slice(this._bufIdx, tmpEnd);
        this._bufIdx = tmpEnd;
        callback(tmp);
        return true;
    }
    getBlob() {
        return null;
    }
    getArray() {
        return this._buf;
    }
}
class BlobBuffer {
    constructor(buf) {
        this._bufIdx = 0;
        this._buf = buf;
    }
    remaining() {
        return this._buf.size - this._bufIdx;
    }
    position() {
        return this._bufIdx;
    }
    size() {
        return this._buf.size;
    }
    reset() {
        this._bufIdx = 0;
    }
    getBytes(length, callback) {
        let tmpSize = this.remaining();
        if (tmpSize > length) {
            tmpSize = length;
        }
        if (tmpSize <= 0) {
            return false;
        }
        let tmpEnd = this._bufIdx + tmpSize;
        let tmp = this._buf.slice(this._bufIdx, tmpEnd);
        let tmpReader = new FileReader();
        tmpReader.onload = (event) => {
            if (event.target) {
                //成功读取
                callback(event.target.result);
            }
        };
        tmpReader.readAsArrayBuffer(tmp);
        this._bufIdx = tmpEnd;
        return true;
    }
    getBlob() {
        return this._buf;
    }
    getArray() {
        return null;
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/Channel.ts":
/*!***********************************************!*\
  !*** ./src/socketd/transport/core/Channel.ts ***!
  \***********************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   ChannelBase: () => (/* binding */ ChannelBase)
/* harmony export */ });
/* harmony import */ var _Frame__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./Frame */ "./src/socketd/transport/core/Frame.ts");

class ChannelBase {
    constructor(config) {
        this._config = config;
        this._attachments = new Map();
        this._isClosed = 0;
    }
    getAttachment(name) {
        return this._attachments.get(name);
    }
    putAttachment(name, val) {
        if (val == null) {
            this._attachments.delete(name);
        }
        else {
            this._attachments.set(name, val);
        }
    }
    isClosed() {
        return this._isClosed;
    }
    close(code) {
        this._isClosed = code;
        this._attachments.clear();
    }
    getConfig() {
        return this._config;
    }
    setHandshake(handshake) {
        this._handshake = handshake;
    }
    getHandshake() {
        return this._handshake;
    }
    sendConnect(url) {
        this.send(_Frame__WEBPACK_IMPORTED_MODULE_0__.Frames.connectFrame(this.getConfig().getIdGenerator().generate(), url), null);
    }
    sendConnack(connectMessage) {
        this.send(_Frame__WEBPACK_IMPORTED_MODULE_0__.Frames.connackFrame(connectMessage), null);
    }
    sendPing() {
        this.send(_Frame__WEBPACK_IMPORTED_MODULE_0__.Frames.pingFrame(), null);
    }
    sendPong() {
        this.send(_Frame__WEBPACK_IMPORTED_MODULE_0__.Frames.pongFrame(), null);
    }
    sendClose() {
        this.send(_Frame__WEBPACK_IMPORTED_MODULE_0__.Frames.closeFrame(), null);
    }
    sendAlarm(from, alarm) {
        this.send(_Frame__WEBPACK_IMPORTED_MODULE_0__.Frames.alarmFrame(from, alarm), null);
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/ChannelDefault.ts":
/*!******************************************************!*\
  !*** ./src/socketd/transport/core/ChannelDefault.ts ***!
  \******************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   ChannelDefault: () => (/* binding */ ChannelDefault)
/* harmony export */ });
/* harmony import */ var _Frame__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./Frame */ "./src/socketd/transport/core/Frame.ts");
/* harmony import */ var _Message__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./Message */ "./src/socketd/transport/core/Message.ts");
/* harmony import */ var _Constants__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./Constants */ "./src/socketd/transport/core/Constants.ts");
/* harmony import */ var _Channel__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./Channel */ "./src/socketd/transport/core/Channel.ts");
/* harmony import */ var _SessionDefault__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./SessionDefault */ "./src/socketd/transport/core/SessionDefault.ts");





class ChannelDefault extends _Channel__WEBPACK_IMPORTED_MODULE_3__.ChannelBase {
    constructor(source, supporter) {
        super(supporter.getConfig());
        this._source = source;
        this._processor = supporter.getProcessor();
        this._assistant = supporter.getAssistant();
        this._streamManger = supporter.getConfig().getStreamManger();
    }
    onOpenFuture(future) {
        this._onOpenFuture = future;
    }
    doOpenFuture(r, e) {
        if (this._onOpenFuture) {
            this._onOpenFuture(r, e);
        }
    }
    isValid() {
        return this.isClosed() == 0 && this._assistant.isValid(this._source);
    }
    config() {
        return this._config;
    }
    sendPing() {
        this.send(_Frame__WEBPACK_IMPORTED_MODULE_0__.Frames.pingFrame(), null);
    }
    sendPong() {
        this.send(_Frame__WEBPACK_IMPORTED_MODULE_0__.Frames.pongFrame(), null);
    }
    send(frame, stream) {
        // if (this.getConfig().clientMode()) {
        //     console.trace("C-SEN:" + frame);
        // } else {
        //     console.trace("S-SEN:" + frame);
        // }
        if (frame.message()) {
            const message = frame.message();
            //注册流接收器
            if (stream != null) {
                this._streamManger.addStream(message.sid(), stream);
            }
            //如果有实体（尝试分片）
            if (message.entity() != null) {
                //确保用完自动关闭
                if (message.dataSize() > this.getConfig().getFragmentSize()) {
                    message.putMeta(_Constants__WEBPACK_IMPORTED_MODULE_2__.EntityMetas.META_DATA_LENGTH, message.dataSize().toString());
                }
                this.getConfig().getFragmentHandler().spliFragment(this, message, fragmentEntity => {
                    //主要是 sid 和 entity
                    const fragmentFrame = new _Frame__WEBPACK_IMPORTED_MODULE_0__.Frame(frame.flag(), new _Message__WEBPACK_IMPORTED_MODULE_1__.MessageBuilder()
                        .flag(frame.flag())
                        .sid(message.sid())
                        .event(message.event())
                        .entity(fragmentEntity)
                        .build());
                    this._assistant.write(this._source, fragmentFrame);
                });
                return;
            }
        }
        //不满足分片条件，直接发
        this._assistant.write(this._source, frame);
    }
    retrieve(frame) {
        const stream = this._streamManger.getStream(frame.message().sid());
        if (stream != null) {
            if (stream.isSingle() || frame.flag() == _Constants__WEBPACK_IMPORTED_MODULE_2__.Flags.ReplyEnd) {
                //如果是单收或者答复结束，则移除流接收器
                this._streamManger.removeStream(frame.message().sid());
            }
            if (stream.isSingle()) {
                //单收时，内部已经是异步机制
                stream.onAccept(frame.message(), this);
            }
            else {
                //改为异步处理，避免卡死Io线程
                stream.onAccept(frame.message(), this);
            }
        }
        else {
            console.debug(`${this.getConfig().getRoleName()} stream not found, sid=${frame.message().sid()}, sessionId=${this.getSession().sessionId()}`);
        }
    }
    reconnect() {
        //由 ClientChannel 实现
    }
    onError(error) {
        this._processor.onError(this, error);
    }
    getSession() {
        if (this._session == null) {
            this._session = new _SessionDefault__WEBPACK_IMPORTED_MODULE_4__.SessionDefault(this);
        }
        return this._session;
    }
    setSession(session) {
        this._session = session;
    }
    close(code) {
        console.debug(`${this.getConfig().getRoleName()} channel will be closed, sessionId=${this.getSession().sessionId()}`);
        try {
            super.close(code);
            this._assistant.close(this._source);
        }
        catch (e) {
            console.warn(`${this.getConfig().getRoleName()} channel close error, sessionId=${this.getSession().sessionId()}`, e);
        }
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/Codec.ts":
/*!*********************************************!*\
  !*** ./src/socketd/transport/core/Codec.ts ***!
  \*********************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   ArrayBufferCodecReader: () => (/* binding */ ArrayBufferCodecReader),
/* harmony export */   ArrayBufferCodecWriter: () => (/* binding */ ArrayBufferCodecWriter)
/* harmony export */ });
class ArrayBufferCodecReader {
    constructor(buf) {
        this._buf = buf;
        this._bufView = new DataView(buf);
        this._bufViewIdx = 0;
    }
    getByte() {
        if (this._bufViewIdx >= this._buf.byteLength) {
            return -1;
        }
        const tmp = this._bufView.getInt8(this._bufViewIdx);
        this._bufViewIdx += 1;
        return tmp;
    }
    getBytes(dst, offset, length) {
        const tmp = new DataView(dst);
        const tmpEndIdx = offset + length;
        for (let i = offset; i < tmpEndIdx; i++) {
            if (this._bufViewIdx >= this._buf.byteLength) {
                //读完了
                break;
            }
            tmp.setInt8(i, this._bufView.getInt8(this._bufViewIdx));
            this._bufViewIdx++;
        }
    }
    getInt() {
        if (this._bufViewIdx >= this._buf.byteLength) {
            return -1;
        }
        const tmp = this._bufView.getInt32(this._bufViewIdx);
        this._bufViewIdx += 4;
        return tmp;
    }
    remaining() {
        return this._buf.byteLength - this._bufViewIdx;
    }
    position() {
        return this._bufViewIdx;
    }
    size() {
        return this._buf.byteLength;
    }
    reset() {
        this._bufViewIdx = 0;
    }
}
class ArrayBufferCodecWriter {
    constructor(n) {
        this._buf = new ArrayBuffer(n);
        this._bufView = new DataView(this._buf);
        this._bufViewIdx = 0;
    }
    putBytes(src) {
        const tmp = new DataView(src);
        const len = tmp.byteLength;
        for (let i = 0; i < len; i++) {
            this._bufView.setInt8(this._bufViewIdx, tmp.getInt8(i));
            this._bufViewIdx += 1;
        }
    }
    putInt(val) {
        this._bufView.setInt32(this._bufViewIdx, val);
        this._bufViewIdx += 4;
    }
    putChar(val) {
        this._bufView.setInt16(this._bufViewIdx, val);
        this._bufViewIdx += 2;
    }
    flush() {
    }
    getBuffer() {
        return this._buf;
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/CodecByteBuffer.ts":
/*!*******************************************************!*\
  !*** ./src/socketd/transport/core/CodecByteBuffer.ts ***!
  \*******************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   CodecByteBuffer: () => (/* binding */ CodecByteBuffer)
/* harmony export */ });
/* harmony import */ var _Frame__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./Frame */ "./src/socketd/transport/core/Frame.ts");
/* harmony import */ var _utils_StrUtils__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../utils/StrUtils */ "./src/socketd/utils/StrUtils.ts");
/* harmony import */ var _Asserts__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./Asserts */ "./src/socketd/transport/core/Asserts.ts");
/* harmony import */ var _Constants__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./Constants */ "./src/socketd/transport/core/Constants.ts");
/* harmony import */ var _Message__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./Message */ "./src/socketd/transport/core/Message.ts");
/* harmony import */ var _Entity__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./Entity */ "./src/socketd/transport/core/Entity.ts");






/**
 * 编解码器（基于 CodecReader,CodecWriter 接口编解）
 *
 * @author noear
 * @since 2.0
 */
class CodecByteBuffer {
    constructor(config) {
        this._config = config;
    }
    /**
     * 解码写入
     *
     * @param frame         帧
     * @param targetFactory 目标工厂
     */
    write(frame, targetFactory) {
        if (frame.message()) {
            //sid
            const sidB = _utils_StrUtils__WEBPACK_IMPORTED_MODULE_1__.StrUtils.strToBuf(frame.message().sid(), this._config.getCharset());
            //event
            const eventB = _utils_StrUtils__WEBPACK_IMPORTED_MODULE_1__.StrUtils.strToBuf(frame.message().event(), this._config.getCharset());
            //metaString
            const metaStringB = _utils_StrUtils__WEBPACK_IMPORTED_MODULE_1__.StrUtils.strToBuf(frame.message().metaString(), this._config.getCharset());
            //length (len[int] + flag[int] + sid + event + metaString + data + \n*3)
            const frameSize = 4 + 4 + sidB.byteLength + eventB.byteLength + metaStringB.byteLength + frame.message().dataSize() + 2 * 3;
            _Asserts__WEBPACK_IMPORTED_MODULE_2__.Asserts.assertSize("sid", sidB.byteLength, _Constants__WEBPACK_IMPORTED_MODULE_3__.Constants.MAX_SIZE_SID);
            _Asserts__WEBPACK_IMPORTED_MODULE_2__.Asserts.assertSize("event", eventB.byteLength, _Constants__WEBPACK_IMPORTED_MODULE_3__.Constants.MAX_SIZE_EVENT);
            _Asserts__WEBPACK_IMPORTED_MODULE_2__.Asserts.assertSize("metaString", metaStringB.byteLength, _Constants__WEBPACK_IMPORTED_MODULE_3__.Constants.MAX_SIZE_META_STRING);
            _Asserts__WEBPACK_IMPORTED_MODULE_2__.Asserts.assertSize("data", frame.message().dataSize(), _Constants__WEBPACK_IMPORTED_MODULE_3__.Constants.MAX_SIZE_DATA);
            const target = targetFactory(frameSize);
            //长度
            target.putInt(frameSize);
            //flag
            target.putInt(frame.flag());
            //sid
            target.putBytes(sidB);
            target.putChar('\n'.charCodeAt(0));
            //event
            target.putBytes(eventB);
            target.putChar('\n'.charCodeAt(0));
            //metaString
            target.putBytes(metaStringB);
            target.putChar('\n'.charCodeAt(0));
            //data
            target.putBytes(frame.message().data().getArray());
            target.flush();
            return target;
        }
        else {
            //length (len[int] + flag[int])
            const frameSize = 4 + 4;
            const target = targetFactory(frameSize);
            //长度
            target.putInt(frameSize);
            //flag
            target.putInt(frame.flag());
            target.flush();
            return target;
        }
    }
    /**
     * 编码读取
     *
     * @param buffer 缓冲
     */
    read(buffer) {
        const frameSize = buffer.getInt();
        if (frameSize > (buffer.remaining() + 4)) {
            return null;
        }
        const flag = buffer.getInt();
        if (frameSize == 8) {
            //len[int] + flag[int]
            return new _Frame__WEBPACK_IMPORTED_MODULE_0__.Frame(_Constants__WEBPACK_IMPORTED_MODULE_3__.Flags.of(flag), null);
        }
        else {
            const metaBufSize = Math.min(_Constants__WEBPACK_IMPORTED_MODULE_3__.Constants.MAX_SIZE_META_STRING, buffer.remaining());
            //1.解码 sid and event
            const buf = new ArrayBuffer(metaBufSize);
            //sid
            const sid = this.decodeString(buffer, buf, _Constants__WEBPACK_IMPORTED_MODULE_3__.Constants.MAX_SIZE_SID);
            //event
            const event = this.decodeString(buffer, buf, _Constants__WEBPACK_IMPORTED_MODULE_3__.Constants.MAX_SIZE_EVENT);
            //metaString
            const metaString = this.decodeString(buffer, buf, _Constants__WEBPACK_IMPORTED_MODULE_3__.Constants.MAX_SIZE_META_STRING);
            //2.解码 body
            const dataRealSize = frameSize - buffer.position();
            let data;
            if (dataRealSize > _Constants__WEBPACK_IMPORTED_MODULE_3__.Constants.MAX_SIZE_DATA) {
                //超界了，空读。必须读，不然协议流会坏掉
                data = new ArrayBuffer(_Constants__WEBPACK_IMPORTED_MODULE_3__.Constants.MAX_SIZE_DATA);
                buffer.getBytes(data, 0, _Constants__WEBPACK_IMPORTED_MODULE_3__.Constants.MAX_SIZE_DATA);
                for (let i = dataRealSize - _Constants__WEBPACK_IMPORTED_MODULE_3__.Constants.MAX_SIZE_DATA; i > 0; i--) {
                    buffer.getByte();
                }
            }
            else {
                data = new ArrayBuffer(dataRealSize);
                if (dataRealSize > 0) {
                    buffer.getBytes(data, 0, dataRealSize);
                }
            }
            //先 data , 后 metaString (避免 data 时修改元信息)
            const message = new _Message__WEBPACK_IMPORTED_MODULE_4__.MessageBuilder()
                .flag(_Constants__WEBPACK_IMPORTED_MODULE_3__.Flags.of(flag))
                .sid(sid)
                .event(event)
                .entity(new _Entity__WEBPACK_IMPORTED_MODULE_5__.EntityDefault().dataSet(data).metaStringSet(metaString))
                .build();
            return new _Frame__WEBPACK_IMPORTED_MODULE_0__.Frame(message.flag(), message);
        }
    }
    decodeString(reader, buf, maxLen) {
        const bufView = new DataView(buf);
        let bufViewIdx = 0;
        while (true) {
            const c = reader.getByte();
            if (c == 10) { //10:'\n'
                break;
            }
            if (maxLen > 0 && maxLen <= bufViewIdx) {
                //超界了，空读。必须读，不然协议流会坏掉
            }
            else {
                if (c != 0) { //32:' '
                    bufView.setInt8(bufViewIdx, c);
                    bufViewIdx++;
                }
            }
        }
        if (bufViewIdx < 1) {
            return "";
        }
        //这里要加个长度控制
        return _utils_StrUtils__WEBPACK_IMPORTED_MODULE_1__.StrUtils.bufToStr(buf, 0, bufViewIdx, this._config.getCharset());
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/Config.ts":
/*!**********************************************!*\
  !*** ./src/socketd/transport/core/Config.ts ***!
  \**********************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   ConfigBase: () => (/* binding */ ConfigBase)
/* harmony export */ });
/* harmony import */ var _Stream__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./Stream */ "./src/socketd/transport/core/Stream.ts");
/* harmony import */ var _IdGenerator__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./IdGenerator */ "./src/socketd/transport/core/IdGenerator.ts");
/* harmony import */ var _FragmentHandler__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./FragmentHandler */ "./src/socketd/transport/core/FragmentHandler.ts");
/* harmony import */ var _Constants__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./Constants */ "./src/socketd/transport/core/Constants.ts");
/* harmony import */ var _Asserts__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./Asserts */ "./src/socketd/transport/core/Asserts.ts");
/* harmony import */ var _CodecByteBuffer__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./CodecByteBuffer */ "./src/socketd/transport/core/CodecByteBuffer.ts");






class ConfigBase {
    constructor(clientMode) {
        this._clientMode = clientMode;
        this._streamManger = new _Stream__WEBPACK_IMPORTED_MODULE_0__.StreamMangerDefault(this);
        this._codec = new _CodecByteBuffer__WEBPACK_IMPORTED_MODULE_5__.CodecByteBuffer(this);
        this._charset = "utf-8";
        this._idGenerator = new _IdGenerator__WEBPACK_IMPORTED_MODULE_1__.GuidGenerator();
        this._fragmentHandler = new _FragmentHandler__WEBPACK_IMPORTED_MODULE_2__.FragmentHandlerDefault();
        this._fragmentSize = _Constants__WEBPACK_IMPORTED_MODULE_3__.Constants.MAX_SIZE_DATA;
        this._coreThreads = 2;
        this._maxThreads = this._coreThreads * 4;
        this._readBufferSize = 512;
        this._writeBufferSize = 512;
        this._idleTimeout = 0; //默认不关（提供用户特殊场景选择）
        this._requestTimeout = 10000; //10秒（默认与连接超时同）
        this._streamTimeout = 1000 * 60 * 60 * 2; //2小时 //避免永不回调时，不能释放
        this._maxUdpSize = 2048; //2k //与 netty 保持一致 //实际可用 1464
    }
    /**
     * 是否客户端模式
     */
    clientMode() {
        return this._clientMode;
    }
    /**
     * 获取流管理器
     */
    getStreamManger() {
        return this._streamManger;
    }
    /**
     * 获取角色名
     * */
    getRoleName() {
        return this.clientMode() ? "Client" : "Server";
    }
    /**
     * 获取字符集
     */
    getCharset() {
        return this._charset;
    }
    /**
     * 配置字符集
     */
    charset(charset) {
        this._charset = charset;
        return this;
    }
    /**
     * 获取编解码器
     */
    getCodec() {
        return this._codec;
    }
    /**
     * 获取标识生成器
     */
    getIdGenerator() {
        return this._idGenerator;
    }
    /**
     * 配置标识生成器
     */
    idGenerator(idGenerator) {
        _Asserts__WEBPACK_IMPORTED_MODULE_4__.Asserts.assertNull("idGenerator", idGenerator);
        this._idGenerator = idGenerator;
        return this;
    }
    /**
     * 获取分片处理
     */
    getFragmentHandler() {
        return this._fragmentHandler;
    }
    /**
     * 配置分片处理
     */
    fragmentHandler(fragmentHandler) {
        _Asserts__WEBPACK_IMPORTED_MODULE_4__.Asserts.assertNull("fragmentHandler", fragmentHandler);
        this._fragmentHandler = fragmentHandler;
        return this;
    }
    /**
     * 获取分片大小
     */
    getFragmentSize() {
        return this._fragmentSize;
    }
    /**
     * 配置分片大小
     */
    fragmentSize(fragmentSize) {
        if (fragmentSize > _Constants__WEBPACK_IMPORTED_MODULE_3__.Constants.MAX_SIZE_DATA) {
            throw new Error("The parameter fragmentSize cannot > 16m");
        }
        if (fragmentSize < _Constants__WEBPACK_IMPORTED_MODULE_3__.Constants.MIN_FRAGMENT_SIZE) {
            throw new Error("The parameter fragmentSize cannot < 1k");
        }
        this._fragmentSize = fragmentSize;
        return this;
    }
    /**
     * 获取核心线程数
     */
    getCoreThreads() {
        return this._coreThreads;
    }
    /**
     * 配置核心线程数
     */
    coreThreads(coreThreads) {
        this._coreThreads = coreThreads;
        this._maxThreads = coreThreads * 4;
        return this;
    }
    /**
     * 获取最大线程数
     */
    getMaxThreads() {
        return this._maxThreads;
    }
    /**
     * 配置最大线程数
     */
    maxThreads(maxThreads) {
        this._maxThreads = maxThreads;
        return this;
    }
    /**
     * 获取读缓冲大小
     */
    getReadBufferSize() {
        return this._readBufferSize;
    }
    /**
     * 配置读缓冲大小
     */
    readBufferSize(readBufferSize) {
        this._readBufferSize = readBufferSize;
        return this;
    }
    /**
     * 获取写缓冲大小
     */
    getWriteBufferSize() {
        return this._writeBufferSize;
    }
    /**
     * 配置写缓冲大小
     */
    writeBufferSize(writeBufferSize) {
        this._writeBufferSize = writeBufferSize;
        return this;
    }
    /**
     * 配置连接空闲超时
     */
    getIdleTimeout() {
        return this._idleTimeout;
    }
    /**
     * 配置连接空闲超时
     */
    idleTimeout(idleTimeout) {
        this._idleTimeout = idleTimeout;
        return this;
    }
    /**
     * 配置请求默认超时
     */
    getRequestTimeout() {
        return this._requestTimeout;
    }
    /**
     * 配置请求默认超时
     */
    requestTimeout(requestTimeout) {
        this._requestTimeout = requestTimeout;
        return this;
    }
    /**
     * 获取消息流超时（单位：毫秒）
     * */
    getStreamTimeout() {
        return this._streamTimeout;
    }
    /**
     * 配置消息流超时（单位：毫秒）
     * */
    streamTimeout(streamTimeout) {
        this._streamTimeout = streamTimeout;
        return this;
    }
    /**
     * 获取允许最大UDP包大小
     */
    getMaxUdpSize() {
        return this._maxUdpSize;
    }
    /**
     * 配置允许最大UDP包大小
     */
    maxUdpSize(maxUdpSize) {
        this._maxUdpSize = maxUdpSize;
        return this;
    }
    /**
     * 生成 id
     * */
    generateId() {
        return this._idGenerator.generate();
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/Constants.ts":
/*!*************************************************!*\
  !*** ./src/socketd/transport/core/Constants.ts ***!
  \*************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   Constants: () => (/* binding */ Constants),
/* harmony export */   EntityMetas: () => (/* binding */ EntityMetas),
/* harmony export */   Flags: () => (/* binding */ Flags)
/* harmony export */ });
/* harmony import */ var _Buffer__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./Buffer */ "./src/socketd/transport/core/Buffer.ts");

/**
 * 常量
 *
 * @author noear
 * @since 2.0
 */
const Constants = {
    /**
     * 默认流id（占位）
     */
    DEF_SID: "",
    /**
     * 默认事件（占位）
     */
    DEF_EVENT: "",
    /**
     * 默认元信息字符串（占位）
     */
    DEF_META_STRING: "",
    /**
     * 默认数据
     * */
    DEF_DATA: new _Buffer__WEBPACK_IMPORTED_MODULE_0__.ByteBuffer(new ArrayBuffer(0)),
    /**
     * 因协议指令关闭
     */
    CLOSE1_PROTOCOL: 1,
    /**
     * 因协议非法关闭
     */
    CLOSE2_PROTOCOL_ILLEGAL: 2,
    /**
     * 因异常关闭
     */
    CLOSE3_ERROR: 3,
    /**
     * 因用户主动关闭
     */
    CLOSE4_USER: 4,
    /**
     * 流ID长度最大限制
     */
    MAX_SIZE_SID: 64,
    /**
     * 事件长度最大限制
     */
    MAX_SIZE_EVENT: 512,
    /**
     * 元信息串长度最大限制
     */
    MAX_SIZE_META_STRING: 4096,
    /**
     * 数据长度最大限制（也是分片长度最大限制）
     */
    MAX_SIZE_DATA: 1024 * 1024 * 16,
    /**
     * 分片长度最小限制
     */
    MIN_FRAGMENT_SIZE: 1024
};
/**
 * 标志
 *
 * @author noear
 * @since 2.0
 */
const Flags = {
    /**
     * 未知
     */
    Unknown: 0,
    /**
     * 连接
     */
    Connect: 10, //握手：连接(c->s)，提交客户端握手信息，请求服务端握手信息
    /**
     * 连接确认
     */
    Connack: 11, //握手：确认(c<-s)，响应服务端握手信息
    /**
     * Ping
     */
    Ping: 20, //心跳:ping(c<->s)
    /**
     * Pong
     */
    Pong: 21, //心跳:pong(c<->s)
    /**
     * 关闭（Udp 没有断链的概念，需要发消息）
     */
    Close: 30,
    /**
     * 告警
     */
    Alarm: 31,
    /**
     * 消息
     */
    Message: 40, //消息(c<->s)
    /**
     * 请求
     */
    Request: 41, //请求(c<->s)
    /**
     * 订阅
     */
    Subscribe: 42,
    /**
     * 答复
     */
    Reply: 48,
    /**
     * 答复结束（结束订阅接收）
     */
    ReplyEnd: 49,
    of: function (code) {
        switch (code) {
            case 10:
                return this.Connect;
            case 11:
                return this.Connack;
            case 20:
                return this.Ping;
            case 21:
                return this.Pong;
            case 30:
                return this.Close;
            case 31:
                return this.Alarm;
            case 40:
                return this.Message;
            case 41:
                return this.Request;
            case 42:
                return this.Subscribe;
            case 48:
                return this.Reply;
            case 49:
                return this.ReplyEnd;
            default:
                return this.Unknown;
        }
    },
    name: function (code) {
        switch (code) {
            case this.Connect:
                return "Connect";
            case this.Connack:
                return "Connack";
            case this.Ping:
                return "Ping";
            case this.Pong:
                return "Pong";
            case this.Close:
                return "Close";
            case this.Alarm:
                return "Alarm";
            case this.Message:
                return "Message";
            case this.Request:
                return "Request";
            case this.Subscribe:
                return "Subscribe";
            case this.Reply:
                return "Reply";
            case this.ReplyEnd:
                return "ReplyEnd";
            default:
                return "Unknown";
        }
    }
};
/**
 * 实体元信息常用名
 *
 * @author noear
 * @since 2.0
 */
const EntityMetas = {
    /**
     * 框架版本号
     */
    META_SOCKETD_VERSION: "SocketD",
    /**
     * 数据长度
     */
    META_DATA_LENGTH: "Data-Length",
    /**
     * 数据类型
     */
    META_DATA_TYPE: "Data-Type",
    /**
     * 数据分片索引
     */
    META_DATA_FRAGMENT_IDX: "Data-Fragment-Idx",
    /**
     * 数据描述之文件名
     */
    META_DATA_DISPOSITION_FILENAME: "Data-Disposition-Filename",
    /**
     * 数据范围开始（相当于分页）
     */
    META_RANGE_START: "Data-Range-Start",
    /**
     * 数据范围长度
     */
    META_RANGE_SIZE: "Data-Range-Size",
};


/***/ }),

/***/ "./src/socketd/transport/core/Entity.ts":
/*!**********************************************!*\
  !*** ./src/socketd/transport/core/Entity.ts ***!
  \**********************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   EntityDefault: () => (/* binding */ EntityDefault),
/* harmony export */   FileEntity: () => (/* binding */ FileEntity),
/* harmony export */   StringEntity: () => (/* binding */ StringEntity)
/* harmony export */ });
/* harmony import */ var _utils_StrUtils__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../utils/StrUtils */ "./src/socketd/utils/StrUtils.ts");
/* harmony import */ var _Codec__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./Codec */ "./src/socketd/transport/core/Codec.ts");
/* harmony import */ var _Constants__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./Constants */ "./src/socketd/transport/core/Constants.ts");
/* harmony import */ var _Buffer__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./Buffer */ "./src/socketd/transport/core/Buffer.ts");
/* harmony import */ var _exception_SocketdException__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../exception/SocketdException */ "./src/socketd/exception/SocketdException.ts");





/**
 * 实体默认实现
 *
 * @author noear
 * @since 2.0
 */
class EntityDefault {
    constructor() {
        this._metaMap = null;
        this._data = _Constants__WEBPACK_IMPORTED_MODULE_2__.Constants.DEF_DATA;
        this._dataAsReader = null;
    }
    /**
     * At
     * */
    at() {
        return this.meta("@");
    }
    /**
     * 设置元信息字符串
     * */
    metaStringSet(metaString) {
        this._metaMap = new URLSearchParams(metaString);
        return this;
    }
    /**
     * 放置元信息字典
     *
     * @param map 元信息字典
     */
    metaMapPut(map) {
        if (map instanceof URLSearchParams) {
            const tmp = map;
            tmp.forEach((val, key, p) => {
                this.metaMap().set(key, val);
            });
        }
        else {
            for (const name of map.prototype) {
                this.metaMap().set(name, map[name]);
            }
        }
        return this;
    }
    /**
     * 放置元信息
     *
     * @param name 名字
     * @param val  值
     */
    metaPut(name, val) {
        this.metaMap().set(name, val);
        return this;
    }
    /**
     * 获取元信息字符串（queryString style）
     */
    metaString() {
        return this.metaMap().toString();
    }
    /**
     * 获取元信息字典
     */
    metaMap() {
        if (this._metaMap == null) {
            this._metaMap = new URLSearchParams();
        }
        return this._metaMap;
    }
    /**
     * 获取元信息
     *
     * @param name 名字
     */
    meta(name) {
        return this.metaMap().get(name);
    }
    /**
     * 获取元信息或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    metaOrDefault(name, def) {
        const val = this.meta(name);
        if (val) {
            return val;
        }
        else {
            return def;
        }
    }
    /**
     * 获取元信息并转为 int
     */
    metaAsInt(name) {
        return parseInt(this.metaOrDefault(name, '0'));
    }
    /**
     * 获取元信息并转为 float
     */
    metaAsFloat(name) {
        return parseFloat(this.metaOrDefault(name, '0'));
    }
    /**
     * 放置元信息
     *
     * @param name 名字
     * @param val  值
     */
    putMeta(name, val) {
        this.metaPut(name, val);
    }
    /**
     * 设置数据
     *
     * @param data 数据
     */
    dataSet(data) {
        if (data instanceof Blob) {
            this._data = new _Buffer__WEBPACK_IMPORTED_MODULE_3__.BlobBuffer(data);
        }
        else {
            this._data = new _Buffer__WEBPACK_IMPORTED_MODULE_3__.ByteBuffer(data);
        }
        return this;
    }
    /**
     * 获取数据（若多次复用，需要reset）
     */
    data() {
        return this._data;
    }
    dataAsReader() {
        if (this._data.getArray() == null) {
            throw new _exception_SocketdException__WEBPACK_IMPORTED_MODULE_4__.SocketdException("Blob does not support dataAsReader");
        }
        if (!this._dataAsReader) {
            this._dataAsReader = new _Codec__WEBPACK_IMPORTED_MODULE_1__.ArrayBufferCodecReader(this._data.getArray());
        }
        return this._dataAsReader;
    }
    /**
     * 获取数据并转成字符串
     */
    dataAsString() {
        if (this._data.getArray() == null) {
            throw new _exception_SocketdException__WEBPACK_IMPORTED_MODULE_4__.SocketdException("Blob does not support dataAsString");
        }
        return _utils_StrUtils__WEBPACK_IMPORTED_MODULE_0__.StrUtils.bufToStrDo(this._data.getArray(), '');
    }
    /**
     * 获取数据长度
     */
    dataSize() {
        return this._data.size();
    }
    /**
     * 释放资源
     */
    release() {
    }
    toString() {
        return "Entity{" +
            "meta='" + this.metaString() + '\'' +
            ", data=byte[" + this.dataSize() + ']' + //避免内容太大，影响打印
            '}';
    }
}
/**
 * 字符串实体
 *
 * @author noear
 * @since 2.0
 */
class StringEntity extends EntityDefault {
    constructor(data) {
        super();
        const dataBuf = _utils_StrUtils__WEBPACK_IMPORTED_MODULE_0__.StrUtils.strToBuf(data);
        this.dataSet(dataBuf);
    }
}
class FileEntity extends EntityDefault {
    constructor(file) {
        super();
        this.dataSet(file);
        this.metaPut(_Constants__WEBPACK_IMPORTED_MODULE_2__.EntityMetas.META_DATA_DISPOSITION_FILENAME, file.name);
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/FragmentAggregator.ts":
/*!**********************************************************!*\
  !*** ./src/socketd/transport/core/FragmentAggregator.ts ***!
  \**********************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   FragmentAggregatorDefault: () => (/* binding */ FragmentAggregatorDefault)
/* harmony export */ });
/* harmony import */ var _Message__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./Message */ "./src/socketd/transport/core/Message.ts");
/* harmony import */ var _Entity__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./Entity */ "./src/socketd/transport/core/Entity.ts");
/* harmony import */ var _Frame__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./Frame */ "./src/socketd/transport/core/Frame.ts");
/* harmony import */ var _FragmentHolder__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./FragmentHolder */ "./src/socketd/transport/core/FragmentHolder.ts");
/* harmony import */ var _Constants__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./Constants */ "./src/socketd/transport/core/Constants.ts");
/* harmony import */ var _exception_SocketdException__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../exception/SocketdException */ "./src/socketd/exception/SocketdException.ts");






/**
 * 分片聚合器
 *
 * @author noear
 * @since 2.0
 */
class FragmentAggregatorDefault {
    constructor(main) {
        //分片列表
        this._fragmentHolders = new Array();
        this._main = main;
        const dataLengthStr = main.meta(_Constants__WEBPACK_IMPORTED_MODULE_4__.EntityMetas.META_DATA_LENGTH);
        if (!dataLengthStr) {
            throw new _exception_SocketdException__WEBPACK_IMPORTED_MODULE_5__.SocketdCodecException("Missing '" + _Constants__WEBPACK_IMPORTED_MODULE_4__.EntityMetas.META_DATA_LENGTH + "' meta, event=" + main.event());
        }
        this._dataLength = parseInt(dataLengthStr);
    }
    /**
     * 获取消息流Id（用于消息交互、分片）
     */
    getSid() {
        return this._main.sid();
    }
    /**
     * 数据流大小
     */
    getDataStreamSize() {
        return this._dataStreamSize;
    }
    /**
     * 数据总长度
     */
    getDataLength() {
        return this._dataLength;
    }
    /**
     * 添加帧
     */
    add(index, message) {
        //添加分片
        this._fragmentHolders.push(new _FragmentHolder__WEBPACK_IMPORTED_MODULE_3__.FragmentHolder(index, message));
        //添加计数
        this._dataStreamSize = this._dataStreamSize + message.dataSize();
    }
    /**
     * 获取聚合后的帧
     */
    get() {
        //排序
        this._fragmentHolders.sort((f1, f2) => {
            if (f1.getIndex() == f2.getIndex()) {
                return 0;
            }
            else if (f1.getIndex() > f2.getIndex()) {
                return 1;
            }
            else {
                return -1;
            }
        });
        //创建聚合流
        const dataBuffer = new ArrayBuffer(this._dataLength);
        const dataBufferView = new DataView(dataBuffer);
        let dataBufferViewIdx = 0;
        //添加分片数据
        for (const fh of this._fragmentHolders) {
            const tmp = new DataView(fh.getMessage().data().getArray());
            for (let i = 0; i < fh.getMessage().data().size(); i++) {
                dataBufferView.setInt8(dataBufferViewIdx, tmp.getInt8(i));
                dataBufferViewIdx++;
            }
        }
        //返回
        return new _Frame__WEBPACK_IMPORTED_MODULE_2__.Frame(this._main.flag(), new _Message__WEBPACK_IMPORTED_MODULE_0__.MessageBuilder()
            .flag(this._main.flag())
            .sid(this._main.sid())
            .event(this._main.event())
            .entity(new _Entity__WEBPACK_IMPORTED_MODULE_1__.EntityDefault().metaMapPut(this._main.metaMap()).dataSet(dataBuffer))
            .build());
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/FragmentHandler.ts":
/*!*******************************************************!*\
  !*** ./src/socketd/transport/core/FragmentHandler.ts ***!
  \*******************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   FragmentHandlerDefault: () => (/* binding */ FragmentHandlerDefault)
/* harmony export */ });
/* harmony import */ var _Entity__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./Entity */ "./src/socketd/transport/core/Entity.ts");
/* harmony import */ var _Constants__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./Constants */ "./src/socketd/transport/core/Constants.ts");
/* harmony import */ var _FragmentAggregator__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./FragmentAggregator */ "./src/socketd/transport/core/FragmentAggregator.ts");



/**
 * 数据分片默认实现（可以重写，把大流先缓存到磁盘以节省内存）
 *
 * @author noear
 * @since 2.0
 */
class FragmentHandlerDefault {
    /**
     * 拆割分片
     *
     * @param channel       通道
     * @param message       总包消息
     * @param consumer 分片消费
     */
    spliFragment(channel, message, consumer) {
        if (message.dataSize() > channel.getConfig().getFragmentSize()) {
            let fragmentIndex = 0;
            this.spliFragmentDo(fragmentIndex, channel, message, consumer);
        }
        else {
            if (message.data().getBlob() == null) {
                consumer(message);
            }
            else {
                message.data().getBytes(channel.getConfig().getFragmentSize(), dataBuffer => {
                    consumer(new _Entity__WEBPACK_IMPORTED_MODULE_0__.EntityDefault().dataSet(dataBuffer).metaMapPut(message.metaMap()));
                });
            }
        }
    }
    spliFragmentDo(fragmentIndex, channel, message, consumer) {
        //获取分片
        fragmentIndex++;
        message.data().getBytes(channel.getConfig().getFragmentSize(), dataBuffer => {
            const fragmentEntity = new _Entity__WEBPACK_IMPORTED_MODULE_0__.EntityDefault().dataSet(dataBuffer);
            if (fragmentIndex == 1) {
                fragmentEntity.metaMapPut(message.metaMap());
            }
            fragmentEntity.metaPut(_Constants__WEBPACK_IMPORTED_MODULE_1__.EntityMetas.META_DATA_FRAGMENT_IDX, fragmentIndex.toString());
            consumer(fragmentEntity);
            this.spliFragmentDo(fragmentIndex, channel, message, consumer);
        });
    }
    /**
     * 聚合分片
     *
     * @param channel       通道
     * @param fragmentIndex 分片索引（传过来信息，不一定有顺序）
     * @param message       分片消息
     */
    aggrFragment(channel, fragmentIndex, message) {
        let aggregator = channel.getAttachment(message.sid());
        if (!aggregator) {
            aggregator = new _FragmentAggregator__WEBPACK_IMPORTED_MODULE_2__.FragmentAggregatorDefault(message);
            channel.putAttachment(aggregator.getSid(), aggregator);
        }
        aggregator.add(fragmentIndex, message);
        if (aggregator.getDataLength() > aggregator.getDataStreamSize()) {
            //长度不够，等下一个分片包
            return null;
        }
        else {
            //重置为聚合帖
            channel.putAttachment(message.sid(), null);
            return aggregator.get();
        }
    }
    aggrEnable() {
        return true;
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/FragmentHolder.ts":
/*!******************************************************!*\
  !*** ./src/socketd/transport/core/FragmentHolder.ts ***!
  \******************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   FragmentHolder: () => (/* binding */ FragmentHolder)
/* harmony export */ });
class FragmentHolder {
    constructor(index, message) {
        this._index = index;
        this._message = message;
    }
    /**
     * 获取顺序位
     */
    getIndex() {
        return this._index;
    }
    /**
     * 获取分片帧
     */
    getMessage() {
        return this._message;
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/Frame.ts":
/*!*********************************************!*\
  !*** ./src/socketd/transport/core/Frame.ts ***!
  \*********************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   Frame: () => (/* binding */ Frame),
/* harmony export */   Frames: () => (/* binding */ Frames)
/* harmony export */ });
/* harmony import */ var _Entity__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./Entity */ "./src/socketd/transport/core/Entity.ts");
/* harmony import */ var _Constants__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./Constants */ "./src/socketd/transport/core/Constants.ts");
/* harmony import */ var _Message__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./Message */ "./src/socketd/transport/core/Message.ts");
/* harmony import */ var _socketd__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../socketd */ "./src/socketd/socketd.ts");




/**
 * 帧（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
class Frame {
    constructor(flag, message) {
        this._flag = flag;
        this._message = message;
    }
    /**
     * 标志（保持与 Message 的获取风格）
     * */
    flag() {
        return this._flag;
    }
    /**
     * 消息
     * */
    message() {
        return this._message;
    }
    toString() {
        return "Frame{" +
            "flag=" + _Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.name(this._flag) +
            ", message=" + this._message +
            '}';
    }
}
/**
 * 帧工厂
 *
 * @author noear
 * @since 2.0
 * */
class Frames {
    /**
     * 构建连接帧
     *
     * @param sid 流Id
     * @param url 连接地址
     */
    static connectFrame(sid, url) {
        const entity = new _Entity__WEBPACK_IMPORTED_MODULE_0__.EntityDefault();
        //添加框架版本号
        entity.metaPut(_Constants__WEBPACK_IMPORTED_MODULE_1__.EntityMetas.META_SOCKETD_VERSION, (0,_socketd__WEBPACK_IMPORTED_MODULE_3__.protocolVersion)());
        return new Frame(_Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Connect, new _Message__WEBPACK_IMPORTED_MODULE_2__.MessageBuilder().sid(sid).event(url).entity(entity).build());
    }
    /**
     * 构建连接确认帧
     *
     * @param connectMessage 连接消息
     */
    static connackFrame(connectMessage) {
        const entity = new _Entity__WEBPACK_IMPORTED_MODULE_0__.EntityDefault();
        //添加框架版本号
        entity.metaPut(_Constants__WEBPACK_IMPORTED_MODULE_1__.EntityMetas.META_SOCKETD_VERSION, (0,_socketd__WEBPACK_IMPORTED_MODULE_3__.protocolVersion)());
        return new Frame(_Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Connack, new _Message__WEBPACK_IMPORTED_MODULE_2__.MessageBuilder().sid(connectMessage.sid()).event(connectMessage.event()).entity(entity).build());
    }
    /**
     * 构建 ping 帧
     */
    static pingFrame() {
        return new Frame(_Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Ping, null);
    }
    /**
     * 构建 pong 帧
     */
    static pongFrame() {
        return new Frame(_Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Pong, null);
    }
    /**
     * 构建关闭帧（一般用不到）
     */
    static closeFrame() {
        return new Frame(_Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Close, null);
    }
    /**
     * 构建告警帧（一般用不到）
     */
    static alarmFrame(from, alarm) {
        const message = new _Message__WEBPACK_IMPORTED_MODULE_2__.MessageBuilder();
        if (from != null) {
            //如果有来源消息，则回传元信息
            message.sid(from.sid());
            message.event(from.event());
            message.entity(new _Entity__WEBPACK_IMPORTED_MODULE_0__.StringEntity(alarm).metaStringSet(from.metaString()));
        }
        else {
            message.entity(new _Entity__WEBPACK_IMPORTED_MODULE_0__.StringEntity(alarm));
        }
        return new Frame(_Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Alarm, message.build());
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/HandshakeDefault.ts":
/*!********************************************************!*\
  !*** ./src/socketd/transport/core/HandshakeDefault.ts ***!
  \********************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   HandshakeDefault: () => (/* binding */ HandshakeDefault)
/* harmony export */ });
/* harmony import */ var _Constants__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./Constants */ "./src/socketd/transport/core/Constants.ts");

class HandshakeDefault {
    constructor(source) {
        this._source = source;
        this._url = new URL(source.event());
        this._version = source.meta(_Constants__WEBPACK_IMPORTED_MODULE_0__.EntityMetas.META_SOCKETD_VERSION);
        this._paramMap = new Map();
        for (const [k, v] of this._url.searchParams) {
            this._paramMap.set(k, v);
        }
    }
    getSource() {
        return this._source;
    }
    param(name) {
        return this._paramMap.get(name);
    }
    paramMap() {
        return this._paramMap;
    }
    paramOrDefault(name, def) {
        const tmp = this.param(name);
        return tmp ? tmp : def;
    }
    paramPut(name, value) {
        this._paramMap.set(name, value);
    }
    uri() {
        return this._url;
    }
    version() {
        return this._version;
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/HeartbeatHandler.ts":
/*!********************************************************!*\
  !*** ./src/socketd/transport/core/HeartbeatHandler.ts ***!
  \********************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   HeartbeatHandlerDefault: () => (/* binding */ HeartbeatHandlerDefault)
/* harmony export */ });
class HeartbeatHandlerDefault {
    constructor(heartbeatHandler) {
        this._heartbeatHandler = heartbeatHandler;
    }
    heartbeat(session) {
        if (this._heartbeatHandler == null) {
            session.sendPing();
        }
        else {
            this._heartbeatHandler(session);
        }
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/IdGenerator.ts":
/*!***************************************************!*\
  !*** ./src/socketd/transport/core/IdGenerator.ts ***!
  \***************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   GuidGenerator: () => (/* binding */ GuidGenerator)
/* harmony export */ });
/* harmony import */ var _utils_StrUtils__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../utils/StrUtils */ "./src/socketd/utils/StrUtils.ts");

class GuidGenerator {
    generate() {
        return _utils_StrUtils__WEBPACK_IMPORTED_MODULE_0__.StrUtils.guid();
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/Listener.ts":
/*!************************************************!*\
  !*** ./src/socketd/transport/core/Listener.ts ***!
  \************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   EventListener: () => (/* binding */ EventListener),
/* harmony export */   PathListener: () => (/* binding */ PathListener),
/* harmony export */   PipelineListener: () => (/* binding */ PipelineListener),
/* harmony export */   SimpleListener: () => (/* binding */ SimpleListener)
/* harmony export */ });
/* harmony import */ var _RouteSelector__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./RouteSelector */ "./src/socketd/transport/core/RouteSelector.ts");

/**
 * 简单监听器（一般用于占位）
 *
 * @author noear
 * @since 2.0
 */
class SimpleListener {
    onOpen(session) {
    }
    onMessage(session, message) {
    }
    onClose(session) {
    }
    onError(session, error) {
    }
}
/**
 * 事件监听器（根据消息事件路由）
 *
 * @author noear
 * @since 2.0
 */
class EventListener {
    constructor(routeSelector) {
        if (routeSelector) {
            this._eventRouteSelector = routeSelector;
        }
        else {
            this._eventRouteSelector = new _RouteSelector__WEBPACK_IMPORTED_MODULE_0__.RouteSelectorDefault();
        }
    }
    doOn(event, consumer) {
        this._eventRouteSelector.put(event, consumer);
        return this;
    }
    doOnOpen(consumer) {
        this._doOnOpen = consumer;
        return this;
    }
    doOnMessage(consumer) {
        this._doOnMessage = consumer;
        return this;
    }
    doOnClose(consumer) {
        this._doOnClose = consumer;
        return this;
    }
    doOnError(consumer) {
        this._doOnError = consumer;
        return this;
    }
    onOpen(session) {
        if (this._doOnOpen) {
            this._doOnOpen(session);
        }
    }
    onMessage(session, message) {
        if (this._doOnMessage) {
            this._doOnMessage(session, message);
        }
        const consumer = this._eventRouteSelector.select(message.event());
        if (consumer) {
            consumer(session, message);
        }
    }
    onClose(session) {
        if (this._doOnClose) {
            this._doOnClose(session);
        }
    }
    onError(session, error) {
        if (this._doOnError) {
            this._doOnError(session, error);
        }
    }
}
/**
 * 路径监听器（根据握手地址路由，一般用于服务端）
 *
 * @author noear
 * @since 2.0
 */
class PathListener {
    constructor(routeSelector) {
        if (routeSelector) {
            this._pathRouteSelector = routeSelector;
        }
        else {
            this._pathRouteSelector = new _RouteSelector__WEBPACK_IMPORTED_MODULE_0__.RouteSelectorDefault();
        }
    }
    /**
     * 路由
     */
    of(path, listener) {
        this._pathRouteSelector.put(path, listener);
        return this;
    }
    /**
     * 数量（二级监听器的数据）
     */
    size() {
        return this._pathRouteSelector.size();
    }
    onOpen(session) {
        const l1 = this._pathRouteSelector.select(session.path());
        if (l1 != null) {
            l1.onOpen(session);
        }
    }
    onMessage(session, message) {
        const l1 = this._pathRouteSelector.select(session.path());
        if (l1 != null) {
            l1.onMessage(session, message);
        }
    }
    onClose(session) {
        const l1 = this._pathRouteSelector.select(session.path());
        if (l1 != null) {
            l1.onClose(session);
        }
    }
    onError(session, error) {
        const l1 = this._pathRouteSelector.select(session.path());
        if (l1 != null) {
            l1.onError(session, error);
        }
    }
}
/**
 * 管道监听器
 *
 * @author noear
 * @since 2.0
 */
class PipelineListener {
    constructor() {
        this._deque = new Array();
    }
    /**
     * 前一个
     */
    prev(listener) {
        this._deque.unshift(listener);
        return this;
    }
    /**
     * 后一个
     */
    next(listener) {
        this._deque.push(listener);
        return this;
    }
    /**
     * 数量（二级监听器的数据）
     * */
    size() {
        return this._deque.length;
    }
    /**
     * 打开时
     *
     * @param session 会话
     */
    onOpen(session) {
        for (const listener of this._deque) {
            listener.onOpen(session);
        }
    }
    /**
     * 收到消息时
     *
     * @param session 会话
     * @param message 消息
     */
    onMessage(session, message) {
        for (const listener of this._deque) {
            listener.onMessage(session, message);
        }
    }
    /**
     * 关闭时
     *
     * @param session 会话
     */
    onClose(session) {
        for (const listener of this._deque) {
            listener.onClose(session);
        }
    }
    /**
     * 出错时
     *
     * @param session 会话
     * @param error   错误信息
     */
    onError(session, error) {
        for (const listener of this._deque) {
            listener.onError(session, error);
        }
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/Message.ts":
/*!***********************************************!*\
  !*** ./src/socketd/transport/core/Message.ts ***!
  \***********************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   MessageBuilder: () => (/* binding */ MessageBuilder),
/* harmony export */   MessageDefault: () => (/* binding */ MessageDefault)
/* harmony export */ });
/* harmony import */ var _Constants__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./Constants */ "./src/socketd/transport/core/Constants.ts");

/**
 * 消息默认实现（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
class MessageBuilder {
    constructor() {
        this._flag = _Constants__WEBPACK_IMPORTED_MODULE_0__.Flags.Unknown;
        this._sid = _Constants__WEBPACK_IMPORTED_MODULE_0__.Constants.DEF_SID;
        this._event = _Constants__WEBPACK_IMPORTED_MODULE_0__.Constants.DEF_EVENT;
        this._entity = null;
    }
    /**
     * 设置标记
     */
    flag(flag) {
        this._flag = flag;
        return this;
    }
    /**
     * 设置流id
     */
    sid(sid) {
        this._sid = sid;
        return this;
    }
    /**
     * 设置事件
     */
    event(event) {
        this._event = event;
        return this;
    }
    /**
     * 设置实体
     */
    entity(entity) {
        this._entity = entity;
        return this;
    }
    /**
     * 构建
     */
    build() {
        return new MessageDefault(this._flag, this._sid, this._event, this._entity);
    }
}
/**
 * 消息默认实现（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
class MessageDefault {
    constructor(flag, sid, event, entity) {
        this._flag = flag;
        this._sid = sid;
        this._event = event;
        this._entity = entity;
    }
    at() {
        return this._entity.at();
    }
    /**
     * 获取标记
     */
    flag() {
        return this._flag;
    }
    /**
     * 是否为请求
     */
    isRequest() {
        return this._flag == _Constants__WEBPACK_IMPORTED_MODULE_0__.Flags.Request;
    }
    /**
     * 是否为订阅
     */
    isSubscribe() {
        return this._flag == _Constants__WEBPACK_IMPORTED_MODULE_0__.Flags.Subscribe;
    }
    /**
     * 是否答复结束
     * */
    isEnd() {
        return this._flag == _Constants__WEBPACK_IMPORTED_MODULE_0__.Flags.ReplyEnd;
    }
    /**
     * 获取消息流Id（用于消息交互、分片）
     */
    sid() {
        return this._sid;
    }
    /**
     * 获取消息事件
     */
    event() {
        return this._event;
    }
    /**
     * 获取消息实体
     */
    entity() {
        return this._entity;
    }
    toString() {
        return "Message{" +
            "sid='" + this._sid + '\'' +
            ", event='" + this._event + '\'' +
            ", entity=" + this._entity +
            '}';
    }
    metaString() {
        return this._entity.metaString();
    }
    metaMap() {
        return this._entity.metaMap();
    }
    meta(name) {
        return this._entity.meta(name);
    }
    metaOrDefault(name, def) {
        return this._entity.metaOrDefault(name, def);
    }
    metaAsInt(name) {
        return this._entity.metaAsInt(name);
    }
    metaAsFloat(name) {
        return this._entity.metaAsFloat(name);
    }
    putMeta(name, val) {
        this._entity.putMeta(name, val);
    }
    data() {
        return this._entity.data();
    }
    dataAsReader() {
        return this._entity.dataAsReader();
    }
    dataAsString() {
        return this._entity.dataAsString();
    }
    dataSize() {
        return this._entity.dataSize();
    }
    release() {
        if (this._entity) {
            this._entity.release();
        }
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/Processor.ts":
/*!*************************************************!*\
  !*** ./src/socketd/transport/core/Processor.ts ***!
  \*************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   ProcessorDefault: () => (/* binding */ ProcessorDefault)
/* harmony export */ });
/* harmony import */ var _Listener__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./Listener */ "./src/socketd/transport/core/Listener.ts");
/* harmony import */ var _Constants__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./Constants */ "./src/socketd/transport/core/Constants.ts");
/* harmony import */ var _exception_SocketdException__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../exception/SocketdException */ "./src/socketd/exception/SocketdException.ts");
/* harmony import */ var _HandshakeDefault__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./HandshakeDefault */ "./src/socketd/transport/core/HandshakeDefault.ts");




class ProcessorDefault {
    constructor() {
        this._listener = new _Listener__WEBPACK_IMPORTED_MODULE_0__.SimpleListener();
    }
    setListener(listener) {
        if (listener != null) {
            this._listener = listener;
        }
    }
    onReceive(channel, frame) {
        // if (channel.getConfig().clientMode()) {
        //     console.trace("C-REV:" + frame);
        // } else {
        //     console.trace("S-REV:" + frame);
        // }
        if (frame.flag() == _Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Connect) {
            channel.setHandshake(new _HandshakeDefault__WEBPACK_IMPORTED_MODULE_3__.HandshakeDefault(frame.message()));
            channel.onOpenFuture((r, err) => {
                if (r && channel.isValid()) {
                    //如果还有效，则发送链接确认
                    try {
                        channel.sendConnack(frame.message()); //->Connack
                    }
                    catch (err) {
                        this.onError(channel, err);
                    }
                }
            });
            this.onOpen(channel);
        }
        else if (frame.flag() == _Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Connack) {
            //if client
            channel.setHandshake(new _HandshakeDefault__WEBPACK_IMPORTED_MODULE_3__.HandshakeDefault(frame.message()));
            this.onOpen(channel);
        }
        else {
            if (channel.getHandshake() == null) {
                channel.close(_Constants__WEBPACK_IMPORTED_MODULE_1__.Constants.CLOSE1_PROTOCOL);
                if (frame.flag() == _Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Close) {
                    //说明握手失败了
                    throw new _exception_SocketdException__WEBPACK_IMPORTED_MODULE_2__.SocketdConnectionException("Connection request was rejected");
                }
                console.warn(`${channel.getConfig().getRoleName()} channel handshake is null, sessionId=${channel.getSession().sessionId()}`);
                return;
            }
            try {
                switch (frame.flag()) {
                    case _Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Ping: {
                        channel.sendPong();
                        break;
                    }
                    case _Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Pong: {
                        break;
                    }
                    case _Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Close: {
                        //关闭通道
                        channel.close(_Constants__WEBPACK_IMPORTED_MODULE_1__.Constants.CLOSE1_PROTOCOL);
                        this.onCloseInternal(channel);
                        break;
                    }
                    case _Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Alarm: {
                        //结束流，并异常通知
                        const exception = new _exception_SocketdException__WEBPACK_IMPORTED_MODULE_2__.SocketdAlarmException(frame.message());
                        const stream = channel.getConfig().getStreamManger().getStream(frame.message().sid());
                        if (stream == null) {
                            this.onError(channel, exception);
                        }
                        else {
                            channel.getConfig().getStreamManger().removeStream(frame.message().sid());
                            stream.onError(exception);
                        }
                        break;
                    }
                    case _Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Message:
                    case _Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Request:
                    case _Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Subscribe: {
                        this.onReceiveDo(channel, frame, false);
                        break;
                    }
                    case _Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.Reply:
                    case _Constants__WEBPACK_IMPORTED_MODULE_1__.Flags.ReplyEnd: {
                        this.onReceiveDo(channel, frame, true);
                        break;
                    }
                    default: {
                        channel.close(_Constants__WEBPACK_IMPORTED_MODULE_1__.Constants.CLOSE2_PROTOCOL_ILLEGAL);
                        this.onCloseInternal(channel);
                    }
                }
            }
            catch (e) {
                this.onError(channel, e);
            }
        }
    }
    onReceiveDo(channel, frame, isReply) {
        //如果启用了聚合!
        if (channel.getConfig().getFragmentHandler().aggrEnable()) {
            //尝试聚合分片处理
            const fragmentIdxStr = frame.message().meta(_Constants__WEBPACK_IMPORTED_MODULE_1__.EntityMetas.META_DATA_FRAGMENT_IDX);
            if (fragmentIdxStr != null) {
                //解析分片索引
                const index = parseInt(fragmentIdxStr);
                const frameNew = channel.getConfig().getFragmentHandler().aggrFragment(channel, index, frame.message());
                if (frameNew == null) {
                    return;
                }
                else {
                    frame = frameNew;
                }
            }
        }
        //执行接收处理
        if (isReply) {
            channel.retrieve(frame);
        }
        else {
            this.onMessage(channel, frame.message());
        }
    }
    onOpen(channel) {
        try {
            this._listener.onOpen(channel.getSession());
            channel.doOpenFuture(true, null);
        }
        catch (e) {
            console.warn(`${channel.getConfig().getRoleName()} channel listener onOpen error`, e);
            channel.doOpenFuture(false, e);
        }
    }
    onMessage(channel, message) {
        try {
            this._listener.onMessage(channel.getSession(), message);
        }
        catch (e) {
            console.warn(`${channel.getConfig().getRoleName()} channel listener onMessage error`, e);
            this.onError(channel, e);
        }
    }
    onClose(channel) {
        if (channel.isClosed() == 0) {
            this.onCloseInternal(channel);
        }
    }
    onCloseInternal(channel) {
        this._listener.onClose(channel.getSession());
    }
    onError(channel, error) {
        this._listener.onError(channel.getSession(), error);
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/RouteSelector.ts":
/*!*****************************************************!*\
  !*** ./src/socketd/transport/core/RouteSelector.ts ***!
  \*****************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   RouteSelectorDefault: () => (/* binding */ RouteSelectorDefault)
/* harmony export */ });
/**
 * 路径映射器默认实现（哈希）
 *
 * @author noear
 * @since 2.0
 */
class RouteSelectorDefault {
    constructor() {
        this._inner = new Map();
    }
    /**
     * 选择
     *
     * @param route 路由
     */
    select(route) {
        return this._inner.get(route);
    }
    /**
     * 放置
     *
     * @param route  路由
     * @param target 目标
     */
    put(route, target) {
        this._inner.set(route, target);
    }
    /**
     * 移除
     *
     * @param route 路由
     */
    remove(route) {
        this._inner.delete(route);
    }
    /**
     * 数量
     */
    size() {
        return this._inner.size;
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/Session.ts":
/*!***********************************************!*\
  !*** ./src/socketd/transport/core/Session.ts ***!
  \***********************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   SessionBase: () => (/* binding */ SessionBase)
/* harmony export */ });
/**
 * 会话基类
 *
 * @author noear
 */
class SessionBase {
    constructor(channel) {
        this._channel = channel;
        this._sessionId = this.generateId();
    }
    sessionId() {
        return this._sessionId;
    }
    name() {
        return this.param("@");
    }
    attrMap() {
        if (this._attrMap == null) {
            this._attrMap = new Map();
        }
        return this._attrMap;
    }
    attrHas(name) {
        if (this._attrMap == null) {
            return false;
        }
        return this._attrMap.has(name);
    }
    attr(name) {
        if (this._attrMap == null) {
            return null;
        }
        return this._attrMap.get(name);
    }
    attrOrDefault(name, def) {
        const tmp = this.attr(name);
        return tmp ? tmp : def;
    }
    attrPut(name, val) {
        this.attrMap().set(name, val);
    }
    generateId() {
        return this._channel.getConfig().getIdGenerator().generate();
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/SessionDefault.ts":
/*!******************************************************!*\
  !*** ./src/socketd/transport/core/SessionDefault.ts ***!
  \******************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   SessionDefault: () => (/* binding */ SessionDefault)
/* harmony export */ });
/* harmony import */ var _Session__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./Session */ "./src/socketd/transport/core/Session.ts");
/* harmony import */ var _Message__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./Message */ "./src/socketd/transport/core/Message.ts");
/* harmony import */ var _Frame__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./Frame */ "./src/socketd/transport/core/Frame.ts");
/* harmony import */ var _Constants__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./Constants */ "./src/socketd/transport/core/Constants.ts");
/* harmony import */ var _Stream__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./Stream */ "./src/socketd/transport/core/Stream.ts");





/**
 * 会话默认实现
 *
 * @author noear
 * @since 2.0
 */
class SessionDefault extends _Session__WEBPACK_IMPORTED_MODULE_0__.SessionBase {
    constructor(channel) {
        super(channel);
    }
    isValid() {
        return this._channel.isValid();
    }
    handshake() {
        return this._channel.getHandshake();
    }
    /**
     * 获取握手参数
     *
     * @param name 名字
     */
    param(name) {
        return this.handshake().param(name);
    }
    /**
     * 获取握手参数或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    paramOrDefault(name, def) {
        return this.handshake().paramOrDefault(name, def);
    }
    /**
     * 获取路径
     */
    path() {
        if (this._pathNew == null) {
            return this.handshake().uri().pathname;
        }
        else {
            return this._pathNew;
        }
    }
    /**
     * 设置新路径
     */
    pathNew(pathNew) {
        this._pathNew = pathNew;
    }
    /**
     * 手动重连（一般是自动）
     */
    reconnect() {
        this._channel.reconnect();
    }
    /**
     * 手动发送 Ping（一般是自动）
     */
    sendPing() {
        this._channel.sendPing();
    }
    sendAlarm(from, alarm) {
        this._channel.sendAlarm(from, alarm);
    }
    /**
     * 发送
     */
    send(event, content) {
        const message = new _Message__WEBPACK_IMPORTED_MODULE_1__.MessageBuilder()
            .sid(this.generateId())
            .event(event)
            .entity(content)
            .build();
        this._channel.send(new _Frame__WEBPACK_IMPORTED_MODULE_2__.Frame(_Constants__WEBPACK_IMPORTED_MODULE_3__.Flags.Message, message), null);
    }
    /**
     * 发送并请求（限为一次答复；指定超时）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     * @param timeout 超时
     */
    sendAndRequest(event, content, consumer, timeout) {
        //异步，用 streamTimeout
        const message = new _Message__WEBPACK_IMPORTED_MODULE_1__.MessageBuilder()
            .sid(this.generateId())
            .event(event)
            .entity(content)
            .build();
        const stream = new _Stream__WEBPACK_IMPORTED_MODULE_4__.StreamRequest(message.sid(), timeout, consumer);
        this._channel.send(new _Frame__WEBPACK_IMPORTED_MODULE_2__.Frame(_Constants__WEBPACK_IMPORTED_MODULE_3__.Flags.Request, message), stream);
        return stream;
    }
    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     * @param timeout 超时
     */
    sendAndSubscribe(event, content, consumer, timeout) {
        const message = new _Message__WEBPACK_IMPORTED_MODULE_1__.MessageBuilder()
            .sid(this.generateId())
            .event(event)
            .entity(content)
            .build();
        const stream = new _Stream__WEBPACK_IMPORTED_MODULE_4__.StreamSubscribe(message.sid(), timeout, consumer);
        this._channel.send(new _Frame__WEBPACK_IMPORTED_MODULE_2__.Frame(_Constants__WEBPACK_IMPORTED_MODULE_3__.Flags.Subscribe, message), stream);
        return stream;
    }
    /**
     * 答复
     *
     * @param from    来源消息
     * @param content 内容
     */
    reply(from, content) {
        const message = new _Message__WEBPACK_IMPORTED_MODULE_1__.MessageBuilder()
            .sid(from.sid())
            .event(from.event())
            .entity(content)
            .build();
        this._channel.send(new _Frame__WEBPACK_IMPORTED_MODULE_2__.Frame(_Constants__WEBPACK_IMPORTED_MODULE_3__.Flags.Reply, message), null);
    }
    /**
     * 答复并结束（即最后一次答复）
     *
     * @param from    来源消息
     * @param content 内容
     */
    replyEnd(from, content) {
        const message = new _Message__WEBPACK_IMPORTED_MODULE_1__.MessageBuilder()
            .sid(from.sid())
            .event(from.event())
            .entity(content)
            .build();
        this._channel.send(new _Frame__WEBPACK_IMPORTED_MODULE_2__.Frame(_Constants__WEBPACK_IMPORTED_MODULE_3__.Flags.ReplyEnd, message), null);
    }
    /**
     * 关闭
     */
    close() {
        console.debug(`${this._channel.getConfig().getRoleName()} session will be closed, sessionId=${this.sessionId()}`);
        if (this._channel.isValid()) {
            try {
                this._channel.sendClose();
            }
            catch (e) {
                console.warn(`${this._channel.getConfig().getRoleName()} channel sendClose error`, e);
            }
        }
        this._channel.close(_Constants__WEBPACK_IMPORTED_MODULE_3__.Constants.CLOSE4_USER);
    }
}


/***/ }),

/***/ "./src/socketd/transport/core/Stream.ts":
/*!**********************************************!*\
  !*** ./src/socketd/transport/core/Stream.ts ***!
  \**********************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   StreamBase: () => (/* binding */ StreamBase),
/* harmony export */   StreamMangerDefault: () => (/* binding */ StreamMangerDefault),
/* harmony export */   StreamRequest: () => (/* binding */ StreamRequest),
/* harmony export */   StreamSubscribe: () => (/* binding */ StreamSubscribe)
/* harmony export */ });
/* harmony import */ var _exception_SocketdException__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../exception/SocketdException */ "./src/socketd/exception/SocketdException.ts");
/* harmony import */ var _Asserts__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./Asserts */ "./src/socketd/transport/core/Asserts.ts");


/**
 * 流基类
 *
 * @author noear
 * @since 2.0
 */
class StreamBase {
    constructor(sid, isSingle, timeout) {
        this._sid = sid;
        this._isSingle = isSingle;
        this._timeout = timeout;
    }
    sid() {
        return this._sid;
    }
    isSingle() {
        return this._isSingle;
    }
    timeout() {
        return this._timeout;
    }
    /**
     * 保险开始（避免永久没有回调，造成内存不能释放）
     *
     * @param streamManger  流管理器
     * @param streamTimeout 流超时
     */
    insuranceStart(streamManger, streamTimeout) {
        if (this._insuranceFuture) {
            return;
        }
        this._insuranceFuture = setTimeout(() => {
            streamManger.removeStream(this.sid());
            this.onError(new _exception_SocketdException__WEBPACK_IMPORTED_MODULE_0__.SocketdTimeoutException("The stream response timeout, sid=" + this.sid()));
        }, streamTimeout);
    }
    /**
     * 保险取消息
     */
    insuranceCancel() {
        if (this._insuranceFuture) {
            clearTimeout(this._insuranceFuture);
        }
    }
    /**
     * 异常时
     *
     * @param error 异常
     */
    onError(error) {
        if (this._doOnError != null) {
            this._doOnError(error);
        }
    }
    thenError(onError) {
        this._doOnError = onError;
        return this;
    }
}
/**
 * 请求流
 *
 * @author noear
 * @since 2.0
 */
class StreamRequest extends StreamBase {
    constructor(sid, timeout, future) {
        super(sid, false, timeout);
        this._future = future;
        this._isDone = false;
    }
    isDone() {
        return this._isDone;
    }
    onAccept(reply, channel) {
        this._isDone = true;
        try {
            this._future(reply);
        }
        catch (e) {
            channel.onError(e);
        }
    }
}
/**
 * 订阅流
 *
 * @author noear
 * @since 2.0
 */
class StreamSubscribe extends StreamBase {
    constructor(sid, timeout, future) {
        super(sid, false, timeout);
        this._future = future;
    }
    isDone() {
        return false;
    }
    onAccept(reply, channel) {
        try {
            this._future(reply);
        }
        catch (e) {
            channel.onError(e);
        }
    }
}
class StreamMangerDefault {
    constructor(config) {
        this._config = config;
        this._streamMap = new Map();
    }
    /**
     * 获取流接收器
     *
     * @param sid 流Id
     */
    getStream(sid) {
        return this._streamMap.get(sid);
    }
    /**
     * 添加流接收器
     *
     * @param sid    流Id
     * @param stream 流
     */
    addStream(sid, stream) {
        _Asserts__WEBPACK_IMPORTED_MODULE_1__.Asserts.assertNull("stream", stream);
        this._streamMap.set(sid, stream);
        //增加流超时处理（做为后备保险）
        const streamTimeout = stream.timeout() > 0 ? stream.timeout() : this._config.getStreamTimeout();
        if (streamTimeout > 0) {
            stream.insuranceStart(this, streamTimeout);
        }
    }
    /**
     * 移除流接收器
     *
     * @param sid 流Id
     */
    removeStream(sid) {
        const stream = this.getStream(sid);
        if (stream) {
            this._streamMap.delete(sid);
            stream.insuranceCancel();
            console.debug(`${this._config.getRoleName()} stream removed, sid=${sid}`);
        }
    }
}


/***/ }),

/***/ "./src/socketd/transport_websocket/WsChannelAssistant.ts":
/*!***************************************************************!*\
  !*** ./src/socketd/transport_websocket/WsChannelAssistant.ts ***!
  \***************************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   WsChannelAssistant: () => (/* binding */ WsChannelAssistant)
/* harmony export */ });
/* harmony import */ var _transport_core_Codec__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../transport/core/Codec */ "./src/socketd/transport/core/Codec.ts");

class WsChannelAssistant {
    constructor(config) {
        this._config = config;
    }
    read(buffer) {
        return this._config.getCodec().read(new _transport_core_Codec__WEBPACK_IMPORTED_MODULE_0__.ArrayBufferCodecReader(buffer));
    }
    write(target, frame) {
        let tmp = this._config.getCodec()
            .write(frame, n => new _transport_core_Codec__WEBPACK_IMPORTED_MODULE_0__.ArrayBufferCodecWriter(n));
        target.send(tmp.getBuffer());
    }
    isValid(target) {
        return target.readyState === WebSocket.OPEN;
    }
    close(target) {
        target.close();
    }
    getRemoteAddress(target) {
        throw new Error("Method not implemented.");
    }
    getLocalAddress(target) {
        throw new Error("Method not implemented.");
    }
}


/***/ }),

/***/ "./src/socketd/transport_websocket/WsClient.ts":
/*!*****************************************************!*\
  !*** ./src/socketd/transport_websocket/WsClient.ts ***!
  \*****************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   WsClient: () => (/* binding */ WsClient)
/* harmony export */ });
/* harmony import */ var _transport_client_Client__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../transport/client/Client */ "./src/socketd/transport/client/Client.ts");
/* harmony import */ var _WsChannelAssistant__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./WsChannelAssistant */ "./src/socketd/transport_websocket/WsChannelAssistant.ts");
/* harmony import */ var _WsClientConnector__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./WsClientConnector */ "./src/socketd/transport_websocket/WsClientConnector.ts");



class WsClient extends _transport_client_Client__WEBPACK_IMPORTED_MODULE_0__.ClientBase {
    constructor(clientConfig) {
        super(clientConfig, new _WsChannelAssistant__WEBPACK_IMPORTED_MODULE_1__.WsChannelAssistant(clientConfig));
    }
    createConnector() {
        return new _WsClientConnector__WEBPACK_IMPORTED_MODULE_2__.WsClientConnector(this);
    }
}


/***/ }),

/***/ "./src/socketd/transport_websocket/WsClientConnector.ts":
/*!**************************************************************!*\
  !*** ./src/socketd/transport_websocket/WsClientConnector.ts ***!
  \**************************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   WsClientConnector: () => (/* binding */ WsClientConnector)
/* harmony export */ });
/* harmony import */ var _transport_client_ClientConnector__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../transport/client/ClientConnector */ "./src/socketd/transport/client/ClientConnector.ts");
/* harmony import */ var _impl_WebSocketClientImpl__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./impl/WebSocketClientImpl */ "./src/socketd/transport_websocket/impl/WebSocketClientImpl.ts");


class WsClientConnector extends _transport_client_ClientConnector__WEBPACK_IMPORTED_MODULE_0__.ClientConnectorBase {
    constructor(client) {
        super(client);
    }
    connect() {
        //关闭之前的资源
        this.close();
        //处理自定义架构的影响（重连时，新建实例比原生重链接口靠谱）
        let url = this._client.getConfig().getUrl();
        return new Promise((resolve, reject) => {
            this._real = new _impl_WebSocketClientImpl__WEBPACK_IMPORTED_MODULE_1__.WebSocketClientImpl(url, this._client, (r) => {
                if (r.getThrowable()) {
                    reject(r.getThrowable());
                }
                else {
                    resolve(r.getChannel());
                }
            });
        });
    }
    close() {
        if (this._real) {
            this._real.close();
        }
    }
}


/***/ }),

/***/ "./src/socketd/transport_websocket/WsClientProvider.ts":
/*!*************************************************************!*\
  !*** ./src/socketd/transport_websocket/WsClientProvider.ts ***!
  \*************************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   WsClientProvider: () => (/* binding */ WsClientProvider)
/* harmony export */ });
/* harmony import */ var _WsClient__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./WsClient */ "./src/socketd/transport_websocket/WsClient.ts");

class WsClientProvider {
    schemas() {
        return ["ws", "wss", "sd:ws", "sd:wss"];
    }
    createClient(clientConfig) {
        return new _WsClient__WEBPACK_IMPORTED_MODULE_0__.WsClient(clientConfig);
    }
}


/***/ }),

/***/ "./src/socketd/transport_websocket/impl/WebSocketClientImpl.ts":
/*!*********************************************************************!*\
  !*** ./src/socketd/transport_websocket/impl/WebSocketClientImpl.ts ***!
  \*********************************************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   WebSocketClientImpl: () => (/* binding */ WebSocketClientImpl)
/* harmony export */ });
/* harmony import */ var _transport_client_ClientHandshakeResult__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../transport/client/ClientHandshakeResult */ "./src/socketd/transport/client/ClientHandshakeResult.ts");
/* harmony import */ var _transport_core_ChannelDefault__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../transport/core/ChannelDefault */ "./src/socketd/transport/core/ChannelDefault.ts");
/* harmony import */ var _transport_core_Constants__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../transport/core/Constants */ "./src/socketd/transport/core/Constants.ts");
/* harmony import */ var _exception_SocketdException__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../exception/SocketdException */ "./src/socketd/exception/SocketdException.ts");




class WebSocketClientImpl {
    constructor(url, client, handshakeFuture) {
        this._real = new WebSocket(url);
        this._client = client;
        this._channel = new _transport_core_ChannelDefault__WEBPACK_IMPORTED_MODULE_1__.ChannelDefault(this._real, client);
        this._handshakeFuture = handshakeFuture;
        this._real.binaryType = "arraybuffer";
        this._real.onopen = this.onOpen.bind(this);
        this._real.onmessage = this.onMessage.bind(this);
        this._real.onclose = this.onClose.bind(this);
        this._real.onerror = this.onError.bind(this);
    }
    onOpen(e) {
        try {
            this._channel.sendConnect(this._client.getConfig().getUrl());
        }
        catch (err) {
            console.warn("Client channel sendConnect error", err);
        }
    }
    onMessage(e) {
        if (e.data instanceof String) {
            console.warn("Client channel unsupported onMessage(String test)");
        }
        else {
            try {
                let frame = this._client.getAssistant().read(e.data);
                if (frame != null) {
                    if (frame.flag() == _transport_core_Constants__WEBPACK_IMPORTED_MODULE_2__.Flags.Connack) {
                        this._channel.onOpenFuture((r, err) => {
                            if (err == null) {
                                this._handshakeFuture(new _transport_client_ClientHandshakeResult__WEBPACK_IMPORTED_MODULE_0__.ClientHandshakeResult(this._channel, null));
                            }
                            else {
                                this._handshakeFuture(new _transport_client_ClientHandshakeResult__WEBPACK_IMPORTED_MODULE_0__.ClientHandshakeResult(this._channel, err));
                            }
                        });
                    }
                    this._client.getProcessor().onReceive(this._channel, frame);
                }
            }
            catch (e) {
                if (e instanceof _exception_SocketdException__WEBPACK_IMPORTED_MODULE_3__.SocketdConnectionException) {
                    this._handshakeFuture(new _transport_client_ClientHandshakeResult__WEBPACK_IMPORTED_MODULE_0__.ClientHandshakeResult(this._channel, e));
                }
                console.warn("WebSocket client onMessage error", e);
            }
        }
    }
    onClose(e) {
        this._client.getProcessor().onClose(this._channel);
    }
    onError(e) {
        this._client.getProcessor().onError(this._channel, e);
    }
    close() {
        this._real.close();
    }
}


/***/ }),

/***/ "./src/socketd/utils/RunUtils.ts":
/*!***************************************!*\
  !*** ./src/socketd/utils/RunUtils.ts ***!
  \***************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   RunUtils: () => (/* binding */ RunUtils)
/* harmony export */ });
class RunUtils {
    static runAndTry(fun) {
        try {
            fun();
        }
        catch (e) {
        }
    }
}


/***/ }),

/***/ "./src/socketd/utils/StrUtils.ts":
/*!***************************************!*\
  !*** ./src/socketd/utils/StrUtils.ts ***!
  \***************************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   StrUtils: () => (/* binding */ StrUtils)
/* harmony export */ });
class StrUtils {
    static guid() {
        let guid = "";
        for (let i = 1; i <= 32; i++) {
            const n = Math.floor(Math.random() * 16.0).toString(16);
            guid += n;
        }
        return guid;
    }
    static strToBuf(str, charet) {
        if (!charet) {
            charet = 'utf-8';
        }
        const encoder = new TextEncoder(); // 使用 UTF-8 编码器进行编码
        return encoder.encode(str).buffer; // 将字符串编码成 ArrayBuffer,
    }
    static bufToStr(buf, start, length, charet) {
        if (buf.byteLength != length) {
            //取出子集
            const bufView = new DataView(buf);
            const tmp = new ArrayBuffer(length);
            const tmpView = new DataView(tmp);
            for (let i = 0; i < length; i++) {
                tmpView.setInt8(i, bufView.getInt8(start + i));
            }
            buf = tmp;
        }
        return StrUtils.bufToStrDo(buf, charet);
    }
    static bufToStrDo(buf, charet) {
        if (!charet) {
            charet = 'utf-8';
        }
        const decoder = new TextDecoder(charet);
        return decoder.decode(buf);
    }
}


/***/ })

/******/ 	});
/************************************************************************/
/******/ 	// The module cache
/******/ 	var __webpack_module_cache__ = {};
/******/ 	
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/ 		// Check if module is in cache
/******/ 		var cachedModule = __webpack_module_cache__[moduleId];
/******/ 		if (cachedModule !== undefined) {
/******/ 			return cachedModule.exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = __webpack_module_cache__[moduleId] = {
/******/ 			// no module.id needed
/******/ 			// no module.loaded needed
/******/ 			exports: {}
/******/ 		};
/******/ 	
/******/ 		// Execute the module function
/******/ 		__webpack_modules__[moduleId](module, module.exports, __webpack_require__);
/******/ 	
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/ 	
/************************************************************************/
/******/ 	/* webpack/runtime/define property getters */
/******/ 	(() => {
/******/ 		// define getter functions for harmony exports
/******/ 		__webpack_require__.d = (exports, definition) => {
/******/ 			for(var key in definition) {
/******/ 				if(__webpack_require__.o(definition, key) && !__webpack_require__.o(exports, key)) {
/******/ 					Object.defineProperty(exports, key, { enumerable: true, get: definition[key] });
/******/ 				}
/******/ 			}
/******/ 		};
/******/ 	})();
/******/ 	
/******/ 	/* webpack/runtime/hasOwnProperty shorthand */
/******/ 	(() => {
/******/ 		__webpack_require__.o = (obj, prop) => (Object.prototype.hasOwnProperty.call(obj, prop))
/******/ 	})();
/******/ 	
/******/ 	/* webpack/runtime/make namespace object */
/******/ 	(() => {
/******/ 		// define __esModule on exports
/******/ 		__webpack_require__.r = (exports) => {
/******/ 			if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 				Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 			}
/******/ 			Object.defineProperty(exports, '__esModule', { value: true });
/******/ 		};
/******/ 	})();
/******/ 	
/************************************************************************/
/******/ 	
/******/ 	// startup
/******/ 	// Load entry module and return exports
/******/ 	// This entry module is referenced by other modules so it can't be inlined
/******/ 	var __webpack_exports__ = __webpack_require__("./src/socketd/socketd.ts");
/******/ 	
/******/ })()
;
//# sourceMappingURL=socketd.js.map