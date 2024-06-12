import type {IoBiConsumer, IoConsumer} from "../Typealias";
import type {Session} from "../Session";
import type {Message} from "../Message";
import {RouteSelector, RouteSelectorDefault} from "../RouteSelector";
import {Listener} from "../Listener";


/**
 * 事件监听器（根据消息事件路由）
 *
 * @author noear
 * @since 2.0
 */
export class EventListener implements Listener {
    private _doOnOpen: IoConsumer<Session>;
    private _doOnMessage: IoBiConsumer<Session, Message>;
    private _doOnReply: IoBiConsumer<Session, Message>;
    private _doOnSend: IoBiConsumer<Session, Message>;
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

    onReply(session: Session, message: Message) {
        if (this._doOnReply) {
            this._doOnReply(session, message);
        }
    }

    onSend(session: Session, message: Message) {
        if (this._doOnSend) {
            this._doOnSend(session, message);
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