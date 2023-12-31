"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.PipelineListener = exports.PathListener = exports.EventListener = exports.SimpleListener = void 0;
const RouteSelector_1 = require("./RouteSelector");
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
exports.SimpleListener = SimpleListener;
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
            this._eventRouteSelector = new RouteSelector_1.RouteSelectorDefault();
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
exports.EventListener = EventListener;
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
            this._pathRouteSelector = new RouteSelector_1.RouteSelectorDefault();
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
exports.PathListener = PathListener;
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
exports.PipelineListener = PipelineListener;
