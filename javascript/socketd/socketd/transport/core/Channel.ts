import {Session} from "./Session";
import {Config} from "./Config";
import {HandshakeInternal} from "./Handshake";
import {Frame, Frames, Message} from "./Message";
import {StreamBase} from "./Stream";

export interface Channel {
    /**
     * 获取附件
     */
    getAttachment(name: string): object;

    /**
     * 设置附件
     */
    setAttachment(name: string, val: object);

    /**
     * 是否有效
     */
    isValid();

    /**
     * 是否已关闭
     */
    isClosed(): number;

    /**
     * 关闭（1协议关，2用户关）
     */
    close(code: number);

    /**
     * 获取配置
     */
    getConfig(): Config;

    /**
     * 设置握手信息
     *
     * @param handshake 握手信息
     */
    setHandshake(handshake: HandshakeInternal);

    /**
     * 获取握手信息
     */
    getHandshake(): HandshakeInternal;

    /**
     * 发送连接（握手）
     *
     * @param url 连接地址
     */
    sendConnect(url: string);

    /**
     * 发送连接确认（握手）
     *
     * @param connectMessage 连接消息
     */
    sendConnack(connectMessage: Message);

    /**
     * 发送 Ping（心跳）
     */
    sendPing();

    /**
     * 发送 Pong（心跳）
     */
    sendPong();

    /**
     * 发送 Close
     */
    sendClose();

    /**
     * 发送告警
     */
    sendAlarm(from: Message, alarm: string);


    /**
     * 发送
     *
     * @param frame  帧
     * @param stream 流（没有则为 null）
     */
    send(frame: Frame, stream: StreamBase);

    /**
     * 接收（接收答复帧）
     *
     * @param frame 帧
     */
    retrieve(frame: Frame);

    /**
     * 手动重连（一般是自动）
     */
    reconnect();

    /**
     * 出错时
     */
    onError(error: Error);

    /**
     * 获取会话
     */
    getSession(): Session;
}

abstract class  ChannelBase implements Channel{
    _config: Config;
    _attachments: Map<string, object>;
    _handshake: HandshakeInternal;
    _isClosed: number;

    constructor(config: Config) {
        this._config = config;
        this._attachments = new Map<string, object>();
    }


    getAttachment(name:string):object{
        return this._attachments.get(name);
    }

    setAttachment(name:string, val:object){
        if (val == null) {
            this._attachments.delete(name);
        } else {
            this._attachments.set(name, val);
        }
    }

    isClosed():number{
        return this._isClosed;
    }

    close(code:number){
        this._isClosed = code;
        this._attachments.clear();
    }


    getConfig(): Config {
        return this._config;
    }
    setHandshake(handshake: HandshakeInternal) {
       this._handshake = handshake;
    }
    getHandshake(): HandshakeInternal {
        return this._handshake;
    }
    sendConnect(url:string) {
        this.send(Frames.connectFrame(this.getConfig().generateId(), url),null)
    }
    sendConnack(connectMessage: Message) {
        this.send(Frames.connackFrame(connectMessage), null);
    }
    sendPing() {
        this.send(Frames.pingFrame(), null);
    }
    sendPong() {
        this.send(Frames.pongFrame(), null);
    }
    sendClose() {
        this.send(Frames.closeFrame(), null);
    }
    sendAlarm(from: Message, alarm: string) {
        this.send(Frames.alarmFrame(from, alarm), null);
    }
    send(frame: Frame, stream: StreamBase) {
        throw new Error("Method not implemented.");
    }
    retrieve(frame: Frame) {
        throw new Error("Method not implemented.");
    }
    reconnect() {
        throw new Error("Method not implemented.");
    }
    onError(error: Error) {
        throw new Error("Method not implemented.");
    }
    getSession(): Session {
        throw new Error("Method not implemented.");
    }
}

export class ChannelDefault extends ChannelBase implements Channel{
    _real:WebSocket;
    _config:Config;
    _closeCode:number;
    _handshake:any;
    constructor(real: WebSocket, config: Config) {
        this._real = real;
        this._config = config;
        this._closeCode = 0;
        this._handshake = null;
    }

    config(): Config {
        return this._config;
    }

    sendPing() {
        this.send({flag: Flags.Ping});
    }

    sendPong() {
        this.send({flag: Flags.Pong});
    }

    send(frame) {
        this._config.codec().write(frame, null);
    }

    getSession(): Session {
        return null;
    }

    close(code) {
        this._closeCode = code;
    }
}
