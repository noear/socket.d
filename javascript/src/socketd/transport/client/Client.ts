import {Consumer} from "../../utils/Consumer";
import {ClientConfig} from "./ClientConfig";
import {Listener} from "../core/Listener";
import {Session} from "../core/Session";
import {BiConsumer} from "../../utils/BiConsumer";
import {Message} from "../core/Message";

export interface Client {

    config(consumer: Consumer<ClientConfig>): Client;

    listen(listener: Listener): Client;

    onOpen(fun: Consumer<Session>): Client;

    onMessage(fun: BiConsumer<Session, Message>): Client;

    on(topic: string, fun: BiConsumer<Session, Message>): Client;

    onClose(fun: Consumer<Session>): Client;

    onError(fun: BiConsumer<Session, Error>): Client;

    open(): Session;
}
