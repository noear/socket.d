import type {Entity} from "./Entity";
import type {Message} from "./Message";
import type {Channel} from "./Channel";
import {RequestStream, SendStream, SubscribeStream} from "../stream/Stream";
import type {ClientSession} from "../client/ClientSession";
import type {Handshake} from "./Handshake";
import {SocketAddress} from "./SocketAddress";


/**
 * 会话
 *
 * @author noear
 * @since 2.0
 */
export interface Session extends ClientSession {
    /**
     * 获取远程地址
     */
    remoteAddress(): SocketAddress | null;

    /**
     * 获取本地地址
     */
    localAddress(): SocketAddress | null;

    /**
     * 最后活动时间
     */
    liveTime(): number;

    /**
     * 获取握手信息
     */
    handshake(): Handshake;

    /**
     * broker player name
     *
     * @since 2.1
     */
    name(): string | null;

    /**
     * 获取握手参数
     *
     * @param name 名字
     */
    param(name: string): string | null;

    /**
     * 获取握手参数或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    paramOrDefault(name: string, def: string): string;

    /**
     * 获取握手路径
     */
    path(): string;

    /**
     * 设置握手新路径
     */
    pathNew(pathNew: string);

    /**
     * 获取所有属性
     */
    attrMap(): Map<string, any>;

    /**
     * 是有属性
     *
     * @param name 名字
     */
    attrHas(name: string);

    /**
     * 获取属性
     *
     * @param name 名字
     */
    attr(name: string): any;

    /**
     * 获取属性或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    attrOrDefault(name: string, def: object): object;

    /**
     * 设置属性
     *
     * @param name  名字
     * @param val 值
     */
    attrPut(name: string, val: object);

    /**
     * 手动发送 Ping（一般是自动）
     */
    sendPing();

    /**
     * 发送告警
     */
    sendAlarm(from: Message, alarm: Entity | string);

    /**
     * 答复
     *
     * @param from    来源消息
     * @param entity  实体
     */
    reply(from: Message, entity: Entity);

    /**
     * 答复并结束（即最后一次答复）
     *
     * @param from    来源消息
     * @param entity  实体
     */
    replyEnd(from: Message, entity: Entity);
}

/**
 * 会话基类
 *
 * @author noear
 */
export abstract class SessionBase implements Session {
    protected _channel: Channel;
    private _sessionId: string;
    private _attrMap: Map<string, object> | null;

    constructor(channel: Channel) {
        this._channel = channel;
        this._attrMap = null;
        this._sessionId = this.generateId();
    }

    sessionId(): string {
        return this._sessionId;
    }

    liveTime(): number {
        return this._channel.getLiveTime();
    }

    name(): string | null {
        let tmp = this.param("@");
        return tmp ? tmp : null;
    }

    attrMap(): Map<string, any> {
        if (this._attrMap == null) {
            this._attrMap = new Map<string, any>();
        }

        return this._attrMap;
    }

    attrHas(name: string) {
        if (this._attrMap == null) {
            return false;
        }

        return this._attrMap.has(name);
    }

    attr(name: string): any {
        if (this._attrMap == null) {
            return null;
        }

        return this._attrMap.get(name);
    }

    attrOrDefault(name: string, def: object): object {
        const tmp = this.attr(name);
        return tmp ? tmp : def;
    }

    attrPut(name: string, val: object) {
        this.attrMap().set(name, val);
    }


    abstract handshake(): Handshake ;

    abstract remoteAddress(): SocketAddress | null;

    abstract localAddress(): SocketAddress | null;

    abstract param(name: string): string | null;

    abstract paramOrDefault(name: string, def: string): string;

    abstract path(): string ;

    abstract pathNew(pathNew: string);

    abstract sendPing();

    abstract sendAlarm(from: Message, alarm: Entity|string);

    abstract reply(from: Message, entity: Entity);

    abstract replyEnd(from: Message, entity: Entity);

    abstract isValid(): boolean ;

    isActive(): boolean {
        return this.isValid() && this.isClosing() == false;
    }

    abstract isClosing(): boolean;

    abstract reconnect();

    abstract send(event: string, entity: Entity): SendStream;

    abstract sendAndRequest(event: string, entity: Entity, timeout?: number): RequestStream;

    abstract sendAndSubscribe(event: string, entity: Entity, timeout?: number): SubscribeStream;

    abstract closeStarting();

    abstract preclose();

    abstract close();

    protected generateId() {
        return this._channel.getConfig().genId();
    }
}
