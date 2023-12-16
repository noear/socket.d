const Flags = {
    Unknown:0,
    Connect: 10,
    Connack: 11,
    Ping: 20,
    Pong: 21,
    Close: 30,
    Alarm: 31,
    Message: 40,
    Request: 41,
    Subscribe: 42,
    Reply: 48,
    ReplyEnd: 49
}

const Constants = {
    CLOSE1_PROTOCOL: 1,
    CLOSE2_PROTOCOL_ILLEGAL: 2,
    CLOSE3_ERROR: 3,
    CLOSE4_USER: 4,
    MAX_SIZE_SID: 64,
    MAX_SIZE_EVENT: 512,
    MAX_SIZE_META_STRING: 4096,
    MAX_SIZE_DATA: 1024 * 1024 * 16,
    MIN_FRAGMENT_SIZE: 1024
}

class Entity {
    constructor() {
        this._metaString = null;
        this._metaMap = null;
        this._buffer = null;
    }

    metaString(): string {
        return this._metaString;
    }

    metaMap(): Map {
        if (!this._metaMap) {
            this._metaMap = new Map();
        }

        return this._metaMap;
    }

    meta(name: string) {
        this.metaMap().get(name);
    }

    data(): ArrayBuffer {
        return this._buffer;
    }
}

class EntityDefault extends Entity{
    constructor() {
        super();
    }
    metaString(metaString: stirng) {
        this._metaString = metaString;
        this._metaMap= null;
    }

    metaMap(metaMap:Map){
        this._metaString= null;
        this._metaMap = metaMap;
    }

    meta(name:stirng, val:string){

    }

    data(buffer) {
        this._buffer = buffer;
    }
}

class StringEntity extends Entity {
    constructor(data: string) {
        super();
        this.data(data);
    }
}

class Message {
    constructor(flag: number, sid: string, event: string, entity: Entity) {
        this._flag = flag;
        this._sid = sid;
        this._event = event;
        this._entity = entity;
    }

    sid(): string {
        return this._sid;
    }

    event(): stirn {
        return this._event;
    }

    isRequest(): boolean {
        return this._flag == Flags.Request;
    }

    isSubscribe(): boolean {
        return this._flag == Flags.Subscribe;
    }

    metaString() {
        return this._entity.metaString();
    }

    data() {
        return this._entity.data();
    }
}

class Frame {
    constructor(flag: number, message: Message) {
        this.flag = flag;
        this.message = message;
    }
}

function SessionAction(session:Session){}
function MessageAction(session:Session, message:Message){}

class CodecByteBuffer {
    write(frame, factory) {
        if (frame.message) {
            //sid
            let sidB = frame.message.sid.getBytes(config.getCharset());
            //event
            let eventB = frame.message.event.getBytes(config.getCharset());
            //metaString
            let metaStringB = frame.message.metaString.getBytes(config.getCharset());

            //length (len[int] + flag[int] + sid + event + metaString + data + \n*3)
            let frameSize = 4 + 4 + sidB.length + eventB.length + metaStringB.length + frame.message.dataSize() + 2 * 3;

            Asserts.assertSize("sid", sidB.length, Constants.MAX_SIZE_SID);
            Asserts.assertSize("event", eventB.length, Constants.MAX_SIZE_EVENT);
            Asserts.assertSize("metaString", metaStringB.length, Constants.MAX_SIZE_META_STRING);
            Asserts.assertSize("data", frame.getMessage().dataSize(), Constants.MAX_SIZE_DATA);

            let target = factory.apply(frameSize);

            //长度
            target.putInt(frameSize);

            //flag
            target.putInt(frame.flag);

            //sid
            target.putBytes(sidB);
            target.putChar('\n');

            //event
            target.putBytes(eventB);
            target.putChar('\n');

            //metaString
            target.putBytes(metaStringB);
            target.putChar('\n');

            //data
            target.putBytes(frame.message.dataAsBytes());

            target.flush();

            return target;
        } else {
            //length (len[int] + flag[int])
            let frameSize = 4 + 4;
            let target = factory.apply(frameSize);

            //长度
            target.putInt(frameSize);

            //flag
            target.putInt(frame.getFlag());
            target.flush();

            return target;
        }
    }

    read(buffer) { //=>Frame

    }
}

class StreamManger {
    constructor() {
        this.acceptorMap = {};
    }

    getAcceptor(sid) {
        return this.acceptorMap[sid];
    }

    addAcceptor(sid, acceptor) {
        this.acceptorMap[sid] = acceptor;
    }

    removeAcceptor(sid) {
        delete this.acceptorMap[sid];
    }
}

class Config {
    constructor() {
        this.codec = new CodecByteBuffer();
        this.streamManger = new StreamManger();
    }

}

class Channel {
    constructor(socket: WebSocket, config: Config) {
        this.socket = socket;
        this.config = config;
        this.closeCode = 0;
        this.handshake = null;
    }

    sendPing() {
        this.send({flag: Flags.Ping});
    }

    sendPong() {
        this.send({flag: Flags.Pong});
    }

    sendConnect(url) {
        this.send({flag: Flags.Connect, message: {sid: '', event: url, entity: {metaString: 'Socket.D=1.0', data: ''}}})
    }

    send(frame) {
        this.config.codec.write(frame);
    }

    close(code) {
        this.closeCode = code;
    }
}

class Processor {
    constructor(client: Client) {
        this.client = client;
    }


    onOpen(channel: Channel) {
        if (this.client.onOpenFun) {
            this.client.onOpenFun(channel.getSession());
        }
    }

    onMessage(channel: Channel, message) {
        if (this.client.onMessageFun) {
            this.client.onMessageFun(channel.getSession(), message);
        }

        let onFun = this.client.onFun[message.event];
        if (onFun) {
            onFun(channel.getSession(), message);
        }
    }

    onCloseInternal(channel: Channel) {

    }

    onClose(channel: Channel) {
        if (this.client.onCloseFun) {
            this.client.onCloseFun(channel.getSession());
        }
    }

    onError(channel: Channel, error: Error) {
        if (this.client.onErrorFun) {
            this.client.onErrorFun(channel.getSession(), error);
        }
    }

    onReceive(channel: Channel, frame) {
        if (frame.flag == Flags.Connect) {
            channel.setHandshake(frame.message);
            channel.onError = this.onError;
            channel.onOpenFuture = function () {
                if (channel.isValid()) {
                    //如果还有效，则发送链接确认
                    try {
                        channel.sendConnack(frame.getMessage()); //->Connack
                    } catch (err) {
                        this.onError(channel, err);
                    }
                }
            }
            this.onOpen(channel);
        } else if (frame.flag == Flags.Connack) {
            //if client
            channel.setHandshake(frame.message);
            this.onOpen(channel);
        } else {
            if (channel.handshake == null) {
                channel.close(Constants.CLOSE1_PROTOCOL);

                if (frame.flag == Flags.Close) {
                    throw new Error("Connection request was rejected");
                }
                return
            }

            try {
                switch (frame.flag) {
                    case Flags.Ping: {
                        channel.sendPong();
                        break;
                    }
                    case Flags.Pong: {
                        break;
                    }
                    case Flags.Close: {
                        //关闭通道
                        channel.close(Constants.CLOSE1_PROTOCOL);
                        this.onCloseInternal(channel);
                        break;
                    }
                    case Flags.Alarm: {
                        //结束流，并异常通知
                        let exception = new Error(frame.getMessage());
                        let acceptor = channel.config.streamManger.getAcceptor(frame.getMessage().sid());
                        if (acceptor == null) {
                            this.onError(channel, exception);
                        } else {
                            channel.config.streamManger.removeAcceptor(frame.getMessage().sid());
                            acceptor.onError(exception);
                        }
                        break;
                    }
                    case Flags.Message:
                    case Flags.Request:
                    case Flags.Subscribe: {
                        this.onReceiveDo(channel, frame, false);
                        break;
                    }
                    case Flags.Reply:
                    case Flags.ReplyEnd: {
                        this.onReceiveDo(channel, frame, true);
                        break;
                    }
                    default: {
                        channel.close(Constants.CLOSE2_PROTOCOL_ILLEGAL);
                        this.onCloseInternal(channel);
                    }
                }
            } catch (e) {
                this.onError(channel, e);
            }
        }
    }

    onReceiveDo(channel: Channel, frame, isReply) {

    }
}

class ClientSession {
    constructor(channel: Channel) {
        this.channel = channel;
    }


    send(event, entity) {

    }

    sendAndRequest(event, entity, callback) {

    }

    sendAndSubscribe(event, entity, callback) {

    }
}

class Session extends ClientSession {
    constructor(channel: Channel) {
        super(channel);
    }

    reply(from: Message, entity: Entity) {

    }

    replyEnd(from: Message, entity: Entity) {

    }
}


class Client {
    constructor(serverUrl: string) {
        this.serverUrl = serverUrl;
        this.config = new Config();
        this.processor = new Processor(this);
    }


    open(): ClientSession {
        let socket = new WebSocket(this.serverUrl);
        let channel = new Channel(socket, this.config);

        socket.onopen = function () {
            channel.sendConnect(this.serverUrl);
        }

        socket.onmessage = function (e) {
            let frame = this.readFrame(e);
            this.processor.onReceive(frame);
        }


        socket.onclose = function (e) {
            this.processor.onClose(e);
        }

        socket.onerror = function (e) {
            this.processor.onError(e);
        }

        return new ClientSession(channel);
    }

    onOpen(fun) {
        this.onOpenFun = fun;
    }

    onMessage(fun) {
        this.onMessageFun = fun;
    }

    on(event, fun) {
        this.onFun[event] = fun;
    }

    onClose(fun) {
        this.onCloseFun = fun;
    }

    onError(fun) {
        this.onErrorFun = fun;
    }
}

let SocketD = {
    createClient: function (serverUrl) {
        return new Client(serverUrl);
    }
}
///////


//服务器的地址
var serverUrl = "sd:ws://127.0.0.1:18080/demoe/websocket/13?guid=2";

let clientSession = SocketD.createClient(serverUrl)
    .onOpen(function (s){

    })
    .on("demo", function (s,m){

    })
    .open();

clientSession.send('demo',new StringEntity('').metaMap({'a':'1','b':'2'}));

clientSession.sendAndRequest('demo',new StringEntity('').metaMap({'a':'1','b':'2'}));
clientSession.sendAndRequest('demo',new StringEntity('').metaMap({'a':'1','b':'2'}),reply=>{

});
clientSession.sendAndSubscribe('demo',new StringEntity('').metaMap({'a':'1','b':'2'}), reply=>{

});
