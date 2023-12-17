import {Session} from "./Session";
import {Message} from "./Message";
import {IoBiConsumer, IoConsumer} from "./Types";

export interface Listener {
    onOpen(session: Session);

    onMessage(session: Session, message: Message);

    onClose(session: Session);

    onError(session: Session, error: Error);
}

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

export class EventListener implements Listener {
    _doOnOpen: IoConsumer<Session>;
    _doOnMessage: IoBiConsumer<Session, Message>;
    _doOn: Map<string, IoBiConsumer<Session, Message>>;
    _doOnClose: IoConsumer<Session>;
    _doOnError: IoBiConsumer<Session, Error>;

    constructor() {
        this._doOn = new Map<string, IoBiConsumer<Session, Message>>();
    }

    doOn(event: string, consumer: IoBiConsumer<Session, Message>): EventListener {
        this._doOn.set(event, consumer);
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

        const consumer = this._doOn.get(message.event());
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