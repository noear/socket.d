interface Consumer<T> {
    (t: T): void
}

interface BiConsumer<S, T> {
    (s: S, t: T): void
}


interface Listener {
    onOpen(session: Session): void;

    onMessage(session: Session, message: Message): void;

    onClose(session: Session): void;

    onError(session: Session, error: Error): void;
}

class SimpleListener implements Listener {
    onOpen(session: Session) {
    }

    onMessage(session: Session, message: Message) {
    }

    onClose(session: Session) {
    }

    onError(session: Session, error: Error) {
    }
}

class Entity {
    metaString?: string
    data?: object
}

class Message {
    constructor(sid: string, topic: string, entity?: Entity) {
        this.sid = sid;
        this.topic = topic;
        this.entity = entity;
    }

    sid: string
    topic: string
    entity?: Entity
}

class Frame {
    constructor(flag: number, message?: Message) {
        this.flag = flag;
        this.message = message;
    }

    flag: number
    message?: Message
}


class ClientConfig {
    readonly url: string
    schema?: string
    replyTimeout?: number

    constructor(url: string) {
        this.url = url;
    }
}


interface Session {
    channel: Channel

    isValid(): boolean

    remoteAddress(): object

    localAddress(): object

    handshake(): object

    param(name: string): string

    paramOrDefault(name: string, value: string): string

    path(): string

    pathNew(pathNew: string): void

    // @ts-ignore
    attrMap(): Map<string, object>

    attr<T>(name: string): T

    attrOrDefault<T>(name: string, def: T): T

    attrSet<T>(name: string, value: T): void

    sessionId(): string

    reconnect(): void

    sendPing(): void

    send(topic: string, entity: Entity): void

    sendAndRequest(topic: string, entity: Entity): Entity

    sendAndSubscribe(topic: string, entity: Entity, consumer: Consumer<Entity>): void

    reply(from: Message, entity: Entity): void

    replyEnd(from: Message, entity: Entity): void
}

class SessionDefault implements Session{
    constructor(channel:Channel) {
        this.channel = channel;
    }

    channel: Channel;

    attr<T>(name: string): T {
        return undefined;
    }

    // @ts-ignore
    attrMap(): Map<string, object> {
        return undefined;
    }

    attrOrDefault<T>(name: string, def: T): T {
        return undefined;
    }

    attrSet<T>(name: string, value: T): void {
    }

    handshake(): object {
        return undefined;
    }

    isValid(): boolean {
        return false;
    }

    localAddress(): object {
        return undefined;
    }

    param(name: string): string {
        return "";
    }

    paramOrDefault(name: string, value: string): string {
        return "";
    }

    path(): string {
        return "";
    }

    pathNew(pathNew: string): void {
    }

    reconnect(): void {
    }

    remoteAddress(): object {
        return undefined;
    }

    reply(from: Message, entity: Entity): void {
    }

    replyEnd(from: Message, entity: Entity): void {
    }

    send(topic: string, entity: Entity): void {
    }

    sendAndRequest(topic: string, entity: Entity): Entity {
        return undefined;
    }

    sendAndSubscribe(topic: string, entity: Entity, consumer: Consumer<Entity>): void {
    }

    sendPing(): void {
    }

    sessionId(): string {
        return "";
    }

}


interface ClientConnector {
    config: ClientConfig

    connect(): Channel
}


interface Channel {
    config: ClientConfig

    setHandshake(handshake: object): void

    getHandshake(): object

    send(frame): void

    setSession(session: Session): void

    getSession(): Session
}

class ClientChannel implements Channel {
    real: Channel
    connector: ClientConnector
    config: ClientConfig;

    constructor(channel: Channel, connector: ClientConnector) {
        this.real = channel;
        this.connector = connector;
    }

    open(): Session {
        return null;
    }

    send(frame: Frame): void {
    }

    getHandshake(): object {
        return undefined;
    }

    setHandshake(handshake: object): void {
    }

    setSession(): Session {
        return undefined;
    }

    getSession(): Session {
        return undefined;
    }
}

class Client {
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

/**
 * let connentor = new ClientConnector(this.config);
 *  return new Session(new ClientChannel(connentor.connect(), connentor));
 * */


const SocketD = {
    createClient(url): Client {
        return new Client(new ClientConfig(url));
    }
}

let session = SocketD.createClient("tcp://xxx.xxx.x")
    .config(cfg => {
        cfg.replyTimeout = 12
    })
    .listen({
        onOpen: function (session) {

        },
        onMessage: function (session, message) {

        },
        onClose: function (session) {

        },
        onError: function (session, error) {

        }
    })
    .open();

session.send("/demo", new Entity());

let entity = session.sendAndRequest("/demo", new Entity());
session.sendAndSubscribe("/demo", new Entity(), entity => {

});