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


type IoConsumer<T> = (t:T) => void
type IoBiConsumer<T1,T2> = (t1:T1, t2:T2 )=> void


class CodecUtils{
    static strToBuf(str:string){
        const encoder = new TextEncoder(); // 使用 UTF-8 编码器进行编码
        const dataView = encoder.encode(str); // 将字符串编码成 Uint8Array 数组
        return dataView;
    }
}

class Asserts{
    static assertSize(name:string, size:number, maxSize:number){

    }
}

interface Entity {
    metaString(): string;

    metaMap(): URLSearchParams;

    meta(name: string): string;

    data(): ArrayBuffer;

    dataSize(): number;
}


class EntityDefault implements Entity {
    private _metaMap: URLSearchParams
    private _data: ArrayBuffer;

    constructor() {
        this._metaMap = null;
        this._data = null;
    }

    metaStringSet(metaString: string) {
        this._metaMap = new URLSearchParams(metaString);
    }

    metaMapSet(metaMap: URLSearchParams) {
        this._metaMap = metaMap;
    }

    metaSet(name: string, value: string) {
        this.metaMap().set(name, value);
    }

    dataSet(data: ArrayBuffer) {
        this._data = data;
    }

    metaString(): string {
        return this.metaMap().toString();
    }

    metaMap(): URLSearchParams {
        if (this._metaMap == null) {
            this._metaMap = new URLSearchParams();
        }

        return this._metaMap;
    }

    meta(name: string): string {
        return this.metaMap().get(name);
    }

    data(): ArrayBuffer {
        return this._data;
    }

    dataSize(): number {
        return this._data.byteLength;
    }
}

class StringEntity extends EntityDefault {
    constructor(data: string) {
        super();
        const dataBuf = CodecUtils.strToBuf(data);
        this.dataSet(dataBuf);
    }
}

interface Reply extends Entity {
    isEnd(): boolean
}

interface Message extends Entity {
    isRequest(): boolean;

    isSubscribe(): boolean;

    sid(): string;

    event(): string;

    entity(): Entity;
}

interface MessageInternal extends Message, Entity, Reply{

}

class MessageDefault implements MessageInternal {
    _flag: number;
    _sid: string;
    _event: string;
    _entity: Entity;

    constructor(flag: number, sid: string, event: string, entity: Entity) {
        this._flag = flag;
        this._sid = sid;
        this._event = event;
        this._entity = entity;
    }

    isRequest(): boolean {
        return this._flag == Flags.Request;
    }

    isSubscribe(): boolean {
        return this._flag == Flags.Subscribe;
    }

    isEnd(): boolean {
        return this._flag == Flags.ReplyEnd;
    }

    sid(): string {
        return this._sid;
    }

    event(): string {
        return this._event;
    }

    entity(): Entity {
        return this._entity;
    }

    metaString(): string {
        return this._entity.metaString();
    }

    metaMap(): URLSearchParams {
        return this._entity.metaMap();
    }

    meta(name: string): string {
        return this._entity.meta(name);
    }

    data(): ArrayBuffer {
        return this._entity.data();
    }

    dataSize(): number {
        return this._entity.dataSize();
    }
}

class Frame {
    _flag: number;
    _message: Message;

    constructor(flag: number, message: Message) {
        this._flag = flag;
        this._message = message;
    }

    flag(): number {
        return this._flag;
    }

    message(): Message {
        return this._message;
    }
}

interface ClientSession {
    send(event: string, entity: Entity);

    sendAndRequest(event: string, entity: Entity, callback: IoConsumer<Reply>);

    sendAndSubscribe(event: string, entity: Entity, callback: IoConsumer<Reply>);
}

interface Session extends ClientSession {
    reply(from: Message, entity: Entity);

    replyEnd(from: Message, entity: Entity);
}


interface Codec {
    write(frame: Frame, factory);

    read(buffer): Frame;
}

class CodecByteBuffer implements Codec {
    write(frame: Frame, factory) {
        if (frame.message()) {
            //sid
            let sidB = CodecUtils.strToBuf(frame.message().sid());
            //event
            let eventB = CodecUtils.strToBuf(frame.message().event());
            //metaString
            let metaStringB = CodecUtils.strToBuf(frame.message().metaString());

            //length (len[int] + flag[int] + sid + event + metaString + data + \n*3)
            let frameSize = 4 + 4 + sidB.length + eventB.length + metaStringB.length + frame.message().dataSize() + 2 * 3;

            Asserts.assertSize("sid", sidB.length, Constants.MAX_SIZE_SID);
            Asserts.assertSize("event", eventB.length, Constants.MAX_SIZE_EVENT);
            Asserts.assertSize("metaString", metaStringB.length, Constants.MAX_SIZE_META_STRING);
            Asserts.assertSize("data", frame.message().dataSize(), Constants.MAX_SIZE_DATA);

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
            target.putBytes(frame.message().data());

            target.flush();

            return target;
        } else {
            //length (len[int] + flag[int])
            let frameSize = 4 + 4;
            let target = factory.apply(frameSize);

            //长度
            target.putInt(frameSize);

            //flag
            target.putInt(frame.flag());
            target.flush();

            return target;
        }
    }

    read(buffer): Frame { //=>Frame
        return null;
    }
}

interface Stream {
    sid(): string;

    isSingle(): boolean;

    isDone(): boolean;

    timeout(): number;

    thenError(): Stream;
}

interface SteamInternal extends Stream {
    onAccept(reply: MessageInternal, channel: Channel);

    onError(error: Error);
}

class StreamManger {
    _streamMap: Map<string, SteamInternal>

    constructor() {
        this._streamMap = new Map<string, SteamInternal>();
    }

    getSteam(sid) {
        return this._streamMap.get(sid);
    }

    addSteam(sid, stream: SteamInternal) {
        this._streamMap.set(sid, stream);
    }

    removeSteam(sid) {
        this._streamMap.delete(sid);
    }
}

class Config {
    _codec: Codec;
    _streamManger: StreamManger;

    constructor() {
        this._codec = new CodecByteBuffer();
        this._streamManger = new StreamManger();
    }

    codec(): Codec {
        return this._codec;
    }

    streamManger(): StreamManger {
        return this._streamManger;
    }
}

interface Channel {
    config(): Config;

    sendPing();

    sendPong();

    sendConnect(url: string);

    send(frame: Frame);

    close(code);
}

class Processor{
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

