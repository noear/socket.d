
import {Constants, Flags} from "./Constants";
import {Entity, Reply} from "./Entity";


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
export class MessageBuilder {
    private _flag: number = Flags.Unknown;
    private _sid: string = Constants.DEF_SID;
    private _event: string = Constants.DEF_EVENT;
    private _entity: Entity = null;

    /**
     * 设置标记
     */
    flag(flag: number): MessageBuilder {
        this._flag = flag;
        return this;
    }

    /**
     * 设置流id
     */
    sid(sid: string): MessageBuilder {
        this._sid = sid;
        return this;
    }

    /**
     * 设置事件
     */
    event(event: string): MessageBuilder {
        this._event = event;
        return this;
    }

    /**
     * 设置实体
     */
    entity(entity: Entity): MessageBuilder {
        this._entity = entity;

        return this;
    }

    /**
     * 构建
     */
    build(): MessageInternal {
        return new MessageDefault(this._flag, this._sid, this._event, this._entity);
    }
}
/**
 * 消息默认实现（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
export class MessageDefault implements MessageInternal {
    private _flag: number;
    private _sid: string;
    private _event: string;
    private _entity: Entity;

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