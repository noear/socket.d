import {RouteSelector, RouteSelectorDefault} from "../RouteSelector";
import type {Session} from "../Session";
import type {Message} from "../Message";
import {Listener} from "../Listener";
import {EventListener} from "./EventListener";

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

    onReply(session: Session, message: Message) {
        const l1 = this._pathRouteSelector.select(session.path());

        if (l1 != null) {
            l1.onReply(session, message);
        }
    }

    onSend(session: Session, message: Message) {
        const l1 = this._pathRouteSelector.select(session.path());

        if (l1 != null) {
            l1.onSend(session, message);
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