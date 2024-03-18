import type {Session} from "./Session";
import type {Config} from "./Config";
import type {HandshakeInternal} from "./Handshake";
import type {Message} from "./Message";
import  {Frames} from "./Frame";
import type {Frame} from "./Frame";
import type {StreamInternal} from "../stream/Stream";
import type {IoBiConsumer} from "./Typealias";
import {SocketAddress} from "./SocketAddress";
import {Constants} from "./Constants";

/**
 * 通道
 *
 * @author noear
 * @since 2.0
 */
export interface Channel {
    /**
     * 获取附件
     */
    getAttachment<T>(name: string): T | null;

    /**
     * 放置附件
     */
    putAttachment(name: string, val: object | null);

    /**
     * 是否有效
     */
    isValid(): boolean;

    /**
     * 是否正在关闭
     * */
    isClosing(): boolean;

    /**
     * 是否已关闭
     */
    isClosed(): number;

    /**
     * 关闭（1协议关，2用户关）
     */
    close(code: number);

    /**
     * 最后活动时间
     */
    getLiveTime(): number;

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
     * 获取远程地址
     */
    getRemoteAddress(): SocketAddress | null;

    /**
     * 获取本地地址
     */
    getLocalAddress(): SocketAddress | null;

    /**
     * 发送连接（握手）
     *
     * @param url 连接地址
     */
    sendConnect(url: string, metaMap: Map<string, string>);

    /**
     * 发送连接确认（握手）
     */
    sendConnack();

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
     *
     * @param code 关闭代码
     */
    sendClose(code: number);

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
    send(frame: Frame, stream: StreamInternal<any> | null);

    /**
     * 接收（接收答复帧）
     *
     * @param frame 帧
     */
    retrieve(frame: Frame, stream: StreamInternal<any> | null);

    /**
     * 手动重连（一般是自动）
     */
    reconnect();

    /**
     * 出错时
     */
    onError(error: any);

    /**
     * 获取会话
     */
    getSession(): Session;
}

/**
 * 通道内部扩展
 *
 * @author noear
 * @since 2.0
 */
export interface ChannelInternal extends Channel {
    /**
     * 设置会话
     * */
    setSession(session: Session);

    /**
     * 更新最后活动时间
     * */
    setLiveTimeAsNow();

    /**
     * 获取流
     */
    getStream(sid: string): StreamInternal<any> | null;

    /**
     * 当打开时
     * */
    onOpenFuture(future: IoBiConsumer<boolean, any>);

    /**
     * 执行打开时
     * */
    doOpenFuture(r: boolean, e: any);
}

export abstract class  ChannelBase implements Channel {
    protected _config: Config;
    private _attachments: Map<string, any>;
    private _handshake: HandshakeInternal;

    constructor(config: Config) {
        this._config = config;
        this._attachments = new Map<string, object>();
    }

    abstract getLiveTime(): number;

    getAttachment<T>(name: string): T | null {
        return this._attachments.get(name);
    }

    putAttachment(name: string, val: object | null) {
        if (val == null) {
            this._attachments.delete(name);
        } else {
            this._attachments.set(name, val);
        }
    }

    abstract isValid(): boolean;

    abstract isClosing(): boolean;

    abstract isClosed(): number;

    close(code: number) {
        if (code > Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING) {
            this._attachments.clear();
        }
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

    sendConnect(url: string, metaMap: Map<string, string>) {
        this.send(Frames.connectFrame(this.getConfig().genId(), url, metaMap), null)
    }

    sendConnack() {
        this.send(Frames.connackFrame(this.getHandshake()), null);
    }

    sendPing() {
        this.send(Frames.pingFrame(), null);
    }

    sendPong() {
        this.send(Frames.pongFrame(), null);
    }

    sendClose(code:number) {
        this.send(Frames.closeFrame(code), null);
    }

    sendAlarm(from: Message, alarm: string) {
        this.send(Frames.alarmFrame(from, alarm), null);
    }

    abstract getRemoteAddress(): SocketAddress | null ;

    abstract getLocalAddress(): SocketAddress | null ;

    abstract send(frame: Frame, stream: StreamInternal<any> | null);

    abstract retrieve(frame: Frame, stream: StreamInternal<any> | null);

    abstract reconnect();

    abstract onError(error: any);

    abstract getSession(): Session ;
}
