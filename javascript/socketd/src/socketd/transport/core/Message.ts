import {CodecUtils} from "./CodecUtils";
import {EntityMetas,Flags} from "./Constants";
import {SocketD} from "../../SocketD";

/**
 * 消息实体（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
export interface Entity {
    /**
     * at
     *
     * @since 2.1
     */
    at();

    /**
     * 获取元信息字符串（queryString style）
     */
    metaString(): string;

    /**
     * 获取元信息字典
     */
    metaMap(): URLSearchParams;

    /**
     * 获取元信息
     */
    meta(name: string): string;

    /**
     * 获取元信息或默认
     */
    metaOrDefault(name: string, def: string): string;

    /**
     * 添加元信息
     * */
    putMeta(name: string, val: string);

    /**
     * 获取数据
     */
    data(): ArrayBuffer;

    /**
     * 获取数据并转为字符串
     */
    dataAsString(): string;

    /**
     * 获取数据长度
     */
    dataSize(): number;

    /**
     * 释放资源
     */
    release();
}

/**
 * 答复实体
 *
 * @author noear
 * @since 2.1
 */
export interface Reply extends Entity {
    /**
     * 流Id
     */
    sid(): string;

    /**
     * 是否答复结束
     */
    isEnd(): boolean
}

/**
 * 实体默认实现
 *
 * @author noear
 * @since 2.0
 */
export class EntityDefault implements Entity {
    private _metaMap: URLSearchParams
    private _data: ArrayBuffer;

    constructor() {
        this._metaMap = null;
        this._data = null;
    }

    /**
     * At
     * */
    at() {
        return this.meta("@");
    }

    /**
     * 设置元信息字符串
     * */
    metaStringSet(metaString: string): EntityDefault {
        this._metaMap = new URLSearchParams(metaString);
        return this;
    }

    /**
     * 放置元信息字典
     *
     * @param metaMap 元信息字典
     */
    metaMapPut(map): EntityDefault {
        for (let name of map.prototype) {
            this.metaMap().set(name, map[name]);
        }
        return this;
    }

    /**
     * 放置元信息
     *
     * @param name 名字
     * @param val  值
     */
    metaPut(name: string, value: string): EntityDefault {
        this.metaMap().set(name, value);
        return this;
    }

    /**
     * 获取元信息字符串（queryString style）
     */
    metaString(): string {
        return this.metaMap().toString();
    }

    /**
     * 获取元信息字典
     */
    metaMap(): URLSearchParams {
        if (this._metaMap == null) {
            this._metaMap = new URLSearchParams();
        }

        return this._metaMap;
    }

    /**
     * 获取元信息
     *
     * @param name 名字
     */
    meta(name: string): string {
        return this.metaMap().get(name);
    }

    /**
     * 获取元信息或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    metaOrDefault(name: string, def: string): string {
        let val = this.meta(name);
        if (val == null) {
            return val;
        } else {
            return def;
        }
    }

    /**
     * 放置元信息
     *
     * @param name 名字
     * @param val  值
     */
    putMeta(name: string, val: string) {
        this.metaPut(name, val);
    }

    /**
     * 设置数据
     *
     * @param data 数据
     */
    dataSet(data: ArrayBuffer): EntityDefault {
        this._data = data;
        return this;
    }

    /**
     * 获取数据（若多次复用，需要reset）
     */
    data(): ArrayBuffer {
        return this._data;
    }

    /**
     * 获取数据并转成字符串
     */
    dataAsString(): string {
        throw new Error("Method not implemented.");
    }

    /**
     * 获取数据长度
     */
    dataSize(): number {
        return this._data.byteLength;
    }

    /**
     * 释放资源
     */
    release() {

    }
}

/**
 * 字符串实体
 *
 * @author noear
 * @since 2.0
 */
export class StringEntity extends EntityDefault implements Entity{
    constructor(data: string) {
        super();
        const dataBuf = CodecUtils.strToBuf(data);
        this.dataSet(dataBuf);
    }
}

/**
 * 消息
 *
 * @author noear
 * @since 2.0
 */
export interface Message extends Entity {
    /**
     * 是否为请求
     */
    isRequest(): boolean;

    /**
     * 是否为订阅
     */
    isSubscribe(): boolean;

    /**
     * 获取消息流Id（用于消息交互、分片）
     */
    sid(): string;

    /**
     * 获取消息事件
     */
    event(): string;

    /**
     * 获取消息实体（有时需要获取实体）
     */
    entity(): Entity;
}

/**
 * @author noear
 * @since 2.0
 */
export interface MessageInternal extends Message, Entity, Reply {
    /**
     * 获取标记
     */
    flag(): number;
}

/**
 * 消息默认实现（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
export class MessageDefault implements MessageInternal {
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

    at() {
        return this._entity.at();
    }

    /**
     * 获取标记
     */
    flag(): number {
        return this._flag;
    }

    /**
     * 是否为请求
     */
    isRequest(): boolean {
        return this._flag == Flags.Request;
    }

    /**
     * 是否为订阅
     */
    isSubscribe(): boolean {
        return this._flag == Flags.Subscribe;
    }

    /**
     * 是否答复结束
     * */
    isEnd(): boolean {
        return this._flag == Flags.ReplyEnd;
    }

    /**
     * 获取消息流Id（用于消息交互、分片）
     */
    sid(): string {
        return this._sid;
    }

    /**
     * 获取消息事件
     */
    event(): string {
        return this._event;
    }

    /**
     * 获取消息实体
     */
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

    metaOrDefault(name: string, def: string): string {
        return this._entity.metaOrDefault(name,def);
    }
    putMeta(name: string, val: string) {
        this._entity.putMeta(name, val);
    }

    data(): ArrayBuffer {
        return this._entity.data();
    }

    dataAsString(): string {
        return this._entity.dataAsString();
    }

    dataSize(): number {
        return this._entity.dataSize();
    }

    release() {

    }
}

/**
 * 帧（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
export class Frame {
    _flag: number;
    _message: MessageInternal;

    constructor(flag: number, message: MessageInternal) {
        this._flag = flag;
        this._message = message;
    }

    /**
     * 标志（保持与 Message 的获取风格）
     * */
    flag(): number {
        return this._flag;
    }

    /**
     * 消息
     * */
    message(): MessageInternal {
        return this._message;
    }
}


/**
 * 帧工厂
 *
 * @author noear
 * @since 2.0
 * */
export class Frames {
    /**
     * 构建连接帧
     *
     * @param sid 流Id
     * @param url 连接地址
     */
    static connectFrame(sid: string, url: string): Frame {
        let entity = new EntityDefault();
        //添加框架版本号
        entity.metaPut(EntityMetas.META_SOCKETD_VERSION, SocketD.protocolVersion());
        return new Frame(Flags.Connect, new MessageDefault(Flags.Connect, sid, url, entity));
    }

    /**
     * 构建连接确认帧
     *
     * @param connectMessage 连接消息
     */
    static connackFrame(connectMessage: Message): Frame {
        let entity = new EntityDefault();
        //添加框架版本号
        entity.metaPut(EntityMetas.META_SOCKETD_VERSION, SocketD.protocolVersion());
        return new Frame(Flags.Connack, new MessageDefault(Flags.Connack, connectMessage.sid(), connectMessage.event(), entity));
    }

    /**
     * 构建 ping 帧
     */
    static pingFrame(): Frame {
        return new Frame(Flags.Ping, null);
    }

    /**
     * 构建 pong 帧
     */
    static pongFrame(): Frame {
        return new Frame(Flags.Pong, null);
    }

    /**
     * 构建关闭帧（一般用不到）
     */
    static closeFrame(): Frame {
        return new Frame(Flags.Close, null);
    }

    /**
     * 构建告警帧（一般用不到）
     */
    static alarmFrame(from: Message, alarm: string): Frame {
        let message;

        if (from != null) {
            let entity = new StringEntity(alarm).metaStringSet(from.metaString());
            message = new MessageDefault(Flags.Alarm, from.sid(), from.event(), entity)
        } else {
            let entity = new StringEntity(alarm);
            message = new MessageDefault(Flags.Alarm, '', '', entity);
        }

        return new Frame(Flags.Alarm, message);
    }
}
