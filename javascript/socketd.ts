interface Consumer<T> {
    (t: T): void
}

interface

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
    constructor(sid: string, entity: Entity){
        this.sid = sid;
        this.entity = entity;
    }
    sid: string
    entity?: Entity
}

class Frame {
    flag: number
    message: Message
    constructor(flag:number, message: Message){
        this.flag = flag;
        this.message = message;
    }
}


class ClientConfig {
    readonly url: string
    schema?: string
    replyTimeout?: number

    constructor(url:string){
        this.url = url;
    }
}

interface Channel {
    config: ClientConfig

    send(frame): void
}

interface Session {
    channel: Channel

    send(topic: string, entity: Entity)

    sendAndRequest(topic: string, entity: Entity): Entity

    sendAndSubscribe(topic: string, entity: Entity, consumer: Consumer<Entity>)
}


interface ClientConnector {
    config: ClientConfig

    connect(): Channel
}

class ClientChannel implements Channel{
    real: Channel
    connector: ClientConnector
    config: ClientConfig;
    constructor(channel: Channel, connector: ClientConnector){
        this.real = channel;
        this.connector = connector;
    }

    send(frame): void {
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

    onOpen(fun):Client{
        return this;
    }

    onMessage(fun):Client{
        return this;
    }

    on(topic:String, ):Client{
        return this;
    }

    onClose(fun):Client{
        return this;
    }

    onError(fun):Client{
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


var SocketD={
    createClient(url) : Client {
        let config = new ClientConfig(url);
        return new Client(url);
    }
}

let session = SocketD.createClient("tcp://xxx.xxx.x")
    .config(cfg=>{
        cfg.replyTimeout=12
    })
    .listen({
        onOpen: function (session){

        },
        onMessage: function (session, message){

        },
        onClose: function (session){

        },
        onError: function (session, error){

        }
    })
    .open();

session.send("/demo", new Entity());

let entity = session.sendAndRequest("/demo", new Entity());
session.sendAndSubscribe("/demo",new Entity(),  entity=>{

});