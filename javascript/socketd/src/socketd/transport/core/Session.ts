import {Entity, Message, Reply} from "./Message";
import {IoConsumer} from "./Types";
import {Channel} from "./Channel";
import {Stream} from "./Stream";
import {ClientSession} from "../client/ClientSession";
import {Handshake} from "./Handshake";


/**
 * 会话
 *
 * @author noear
 * @since 2.0
 */
export interface Session extends ClientSession {
    /**
     * 获取握手信息
     */
    handshake(): Handshake;

    /**
     * broker player name
     *
     * @since 2.1
     */
    name(): string;

    /**
     * 获取握手参数
     *
     * @param name 名字
     */
    param(name: string): string;

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
    attrMap(): Map<string, object>;

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
    attr(name: string): object;

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
    sendAlarm(from: Message, alarm: string);

    /**
     * 答复
     *
     * @param from    来源消息
     * @param content 内容
     */
    reply(from: Message, content: Entity);

    /**
     * 答复并结束（即最后一次答复）
     *
     * @param from    来源消息
     * @param content 内容
     */
    replyEnd(from: Message, content: Entity);
}

/**
 * 会话基类
 *
 * @author noear
 */
export abstract class SessionBase implements Session {
    _channel: Channel;
    _sessionId: string;
    _attrMap: Map<string, object>;

    constructor(channel: Channel) {
        this._channel = channel;
        this._sessionId = this.generateId();
    }

    sessionId(): string {
        return this._sessionId;
    }

    name(): string {
        return this.param("@");
    }

    attrMap(): Map<string, object> {
        if (this._attrMap == null) {
            this._attrMap = new Map<string, object>();
        }

        return this._attrMap;
    }

    attrHas(name: string) {
        return this.attrMap().has(name);
    }

    attr(name: string): object {
        return this.attrMap().get(name);
    }

    attrOrDefault(name: string, def: object): object {
        let val = this.attrMap().get(name);
        if (val) {
            return val;
        } else {
            return def;
        }
    }

    attrPut(name: string, val: object) {
        this.attrMap().set(name, val);
    }

    abstract handshake(): Handshake ;

    abstract param(name: string): string;

    abstract paramOrDefault(name: string, def: string): string;

    abstract path(): string ;

    abstract pathNew(pathNew: string);

    abstract sendPing();

    abstract sendAlarm(from: Message, alarm: string);

    abstract reply(from: Message, entity: Entity);

    abstract replyEnd(from: Message, entity: Entity);

    abstract isValid(): boolean ;

    abstract reconnect();

    abstract send(event: string, content: Entity);

    abstract sendAndRequest(event: string, content: Entity, callback: IoConsumer<Reply>, timeout?: number): Stream;

    abstract sendAndSubscribe(event: string, content: Entity, callback: IoConsumer<Reply>, timeout?: number): Stream;

    abstract close();

    protected generateId() {
        return this._channel.getConfig().getIdGenerator().generate();
    }
}
