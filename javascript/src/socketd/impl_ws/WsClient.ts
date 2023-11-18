import {Client} from "../transport/client/Client";
import {ClientConfig} from "../transport/client/ClientConfig";
import {Consumer} from "../utils/Consumer";
import {Session} from "../transport/core/Session";
import {BiConsumer} from "../utils/BiConsumer";
import {Message} from "../transport/core/Message";
import {Listener} from "../transport/core/Listener";
import {ClientConnector} from "../transport/client/ClientConnector";
import {ClientChannel} from "../transport/client/ClientChannel";
import {SessionDefault} from "../transport/core/impl/SessionDefault";


export class WsClient implements Client{
    _config: ClientConfig
    _onOpen: Consumer<Session>
    _onMessage: BiConsumer<Session, Message>
    _onClose: Consumer<Session>
    _onError: BiConsumer<Session, Error>
    // @ts-ignore
    _onMap: Map<string, BiConsumer<Session, Message>>
    _listener: Listener
    _connector: ClientConnector

    constructor(cfg: ClientConfig) {
        this._config = cfg;
    }

    config(consumer: Consumer<ClientConfig>): Client {
        consumer(this._config);
        return this;
    }

    listen(listener: Listener): Client {
        return this;
    }

    onOpen(fun: Consumer<Session>): Client {
        this._onOpen = fun;
        return this;
    }

    onMessage(fun: BiConsumer<Session, Message>): Client {
        this._onMessage = fun;
        return this;
    }

    on(topic: string, fun: BiConsumer<Session, Message>): Client {
        this._onMap.set(topic, fun)
        return this;
    }

    onClose(fun: Consumer<Session>): Client {
        this._onClose = fun
        return this;
    }

    onError(fun: BiConsumer<Session, Error>): Client {
        this._onError = fun;
        return this;
    }

    open(): Session {
        let channel0 = this._connector.connect();
        let clientChannel: ClientChannel = new ClientChannel(channel0, this._connector);

        //同步握手信息
        clientChannel.setHandshake(channel0.getHandshake());
        let session = new SessionDefault(clientChannel);
        //原始通道切换为带壳的 session
        channel0.setSession(session);

        return session;
    }
}
