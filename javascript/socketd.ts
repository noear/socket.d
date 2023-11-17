interface Consumer<T> {
    (t: T): void
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

interface Channel {
    config: ClientConfig

    send(frame): void
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

    attr<T>(name: string, value: T): void

    sessionId(): string

    reconnect(): void

    sendPing(): void

    send(topic: string, entity: Entity): void

    sendAndRequest(topic: string, entity: Entity): Entity

    sendAndSubscribe(topic: string, entity: Entity, consumer: Consumer<Entity>): void

    reply(from: Message, entity: Entity): void

    replyEnd(from: Message, entity: Entity): void
}


interface ClientConnector {
    config: ClientConfig

    connect(): Session
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
}

class Client {
    _config: ClientConfig

    constructor(url: string) {
        this._config = new ClientConfig(url);
    }

    config(consumer: Consumer<ClientConfig>): Client {
        consumer(this._config);
        return this;
    }

    listen(listener: Listener): Client {
        return this;
    }

    onOpen(fun): Client {
        return this;
    }

    onMessage(fun): Client {
        return this;
    }

    on(topic: string,): Client {
        return this;
    }

    onClose(fun): Client {
        return this;
    }

    onError(fun): Client {
        return this;
    }

    open(): Session {
        return null;
    }
}

/**
 * let connentor = new ClientConnector(this.config);
 *  return new Session(new ClientChannel(connentor.connect(), connentor));
 * */


var SocketD = {
    createClient(url): Client {
        let config = new ClientConfig(url);
        return new Client(url);
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