import type {Session} from "./Session";
import type {Message} from "./Message";
import type {IoBiConsumer, IoConsumer} from "./Typealias";
import {RouteSelector, RouteSelectorDefault} from "./RouteSelector";

/**
 * 监听器
 *
 * @author noear
 * @since 2.0
 */
export interface Listener {
    /**
     * 打开时
     *
     * @param session 会话
     */
    onOpen(session: Session);

    /**
     * 收到消息时
     *
     * @param session 会话
     * @param message 消息
     */
    onMessage(session: Session, message: Message);

    /**
     * 关闭时
     *
     * @param session 会话
     */
    onClose(session: Session);

    /**
     * 出错时
     *
     * @param session 会话
     * @param error   错误信息
     */
    onError(session: Session, error: any);
}

/**
 * 简单监听器（一般用于占位）
 *
 * @author noear
 * @since 2.0
 */
export class SimpleListener implements Listener {
    onOpen(session: Session) {

    }

    onMessage(session: Session, message: Message) {

    }

    onClose(session: Session) {

    }

    onError(session: Session, error: Error) {

    }
}

/**
 * 事件监听器（根据消息事件路由）
 *
 * @author noear
 * @since 2.0
 */
export class EventListener implements Listener {
    private _doOnOpen: IoConsumer<Session>;
    private _doOnMessage: IoBiConsumer<Session, Message>;
    private _doOnClose: IoConsumer<Session>;
    private _doOnError: IoBiConsumer<Session, Error>;


    private _eventRouteSelector: RouteSelector<IoBiConsumer<Session, Message>>

    constructor(routeSelector?: RouteSelector<IoBiConsumer<Session, Message>>) {
        if (routeSelector) {
            this._eventRouteSelector = routeSelector;
        } else {
            this._eventRouteSelector = new RouteSelectorDefault();
        }
    }

    doOn(event: string, consumer: IoBiConsumer<Session, Message>): EventListener {
        this._eventRouteSelector.put(event, consumer);
        return this;
    }

    doOnOpen(consumer: IoConsumer<Session>): EventListener {
        this._doOnOpen = consumer;
        return this;
    }

    doOnMessage(consumer: IoBiConsumer<Session, Message>): EventListener {
        this._doOnMessage = consumer;
        return this;
    }

    doOnClose(consumer: IoConsumer<Session>): EventListener {
        this._doOnClose = consumer;
        return this;
    }

    doOnError(consumer: IoBiConsumer<Session, Error>): EventListener {
        this._doOnError = consumer;
        return this;
    }

    onOpen(session: Session) {
        if (this._doOnOpen) {
            this._doOnOpen(session);
        }
    }

    onMessage(session: Session, message: Message) {
        if (this._doOnMessage) {
            this._doOnMessage(session, message);
        }

        const consumer = this._eventRouteSelector.select(message.event());
        if (consumer) {
            consumer(session, message);
        }
    }

    onClose(session: Session) {
        if (this._doOnClose) {
            this._doOnClose(session);
        }
    }

    onError(session: Session, error: Error) {
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
export class PathListener implements Listener {
    /**
     * 路径路由选择器
     * */
    protected _pathRouteSelector: RouteSelector<Listener>;

    constructor(routeSelector?: RouteSelector<Listener>) {
        if (routeSelector) {
            this._pathRouteSelector = routeSelector;
        } else {
            this._pathRouteSelector = new RouteSelectorDefault();
        }
    }

    /**
     * 路由
     *
     * @param path     路径
     * @param listener 监听器
     * @return self
     */
    doOf(path: string, listener: Listener): PathListener {
        this._pathRouteSelector.put(path, listener);
        return this;
    }

    /**
     * 路由
     *
     * @param path 路径
     * @return 事件监听器
     * @since 2.3
     */
    of(path: string): EventListener {
        const listener = new EventListener();
        this._pathRouteSelector.put(path, listener);
        return listener;
    }

    /**
     * 数量（二级监听器的数据）
     */
    size(): number {
        return this._pathRouteSelector.size();
    }

    onOpen(session: Session) {
        const l1 = this._pathRouteSelector.select(session.path());

        if (l1 != null) {
            l1.onOpen(session);
        }
    }

    onMessage(session: Session, message: Message) {
        const l1 = this._pathRouteSelector.select(session.path());

        if (l1 != null) {
            l1.onMessage(session, message);
        }
    }

    onClose(session: Session) {
        const l1 = this._pathRouteSelector.select(session.path());

        if (l1 != null) {
            l1.onClose(session);
        }
    }

    onError(session: Session, error: Error) {
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
export class PipelineListener implements Listener {
    protected _deque = new Array<Listener>();

    /**
     * 前一个
     */
    prev(listener: Listener): PipelineListener {
        this._deque.unshift(listener);
        return this;
    }

    /**
     * 后一个
     */
    next(listener: Listener): PipelineListener {
        this._deque.push(listener);
        return this;
    }

    /**
     * 数量（二级监听器的数据）
     * */
    size(): number {
        return this._deque.length;
    }

    /**
     * 打开时
     *
     * @param session 会话
     */
    onOpen(session: Session) {
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
    onMessage(session: Session, message: Message) {
        for (const listener of this._deque) {
            listener.onMessage(session, message);
        }
    }

    /**
     * 关闭时
     *
     * @param session 会话
     */
    onClose(session: Session) {
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
    onError(session: Session, error: Error) {
        for (const listener of this._deque) {
            listener.onError(session, error);
        }
    }
}