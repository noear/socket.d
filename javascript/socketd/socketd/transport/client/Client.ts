import {Listener} from "../core/Listener";
import {Session} from "../core/Session";
import {IoBiConsumer, IoConsumer} from "../core/Types";
import {Message} from "../core/Message";
import {ClientSession} from "./ClientSession";
import {ClientConfig} from "./ClientConfig";

export interface Client {
    heartbeatHandler()
    config(configHandler: IoConsumer<ClientConfig>)

    listen(listener: Listener): Client;

    open(): ClientSession;

    //监听扩展

    onOpen(consumer: IoConsumer<Session>): Client;

    onMessage(consumer: IoBiConsumer<Session, Message>): Client;

    on(event, consumer: IoBiConsumer<Session, Message>): Client;

    onClose(consumer: IoConsumer<Session>): Client;

    onError(consumer: IoBiConsumer<Session, Error>): Client;
}
