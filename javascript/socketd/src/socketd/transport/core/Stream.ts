import {MessageInternal, Reply} from "./Message";
import {Channel} from "./Channel";
import {IoConsumer} from "./Types";

/**
 * 流
 *
 * @author noear
 * @since 2.1
 */
export interface Stream {
    /**
     * 流Id
     */
    sid(): string;

    /**
     * 是否单收
     */
    isSingle(): boolean;

    /**
     * 是否完成
     */
    isDone(): boolean;

    /**
     * 超时设定（单位：毫秒）
     */
    timeout(): number;

    /**
     * 异常发生时
     */
    thenError(onError: IoConsumer<Error>): Stream;
}

export interface SteamInternal extends Stream {
    /**
     * 接收时
     *
     * @param reply   答复
     * @param channel 通道
     */
    onAccept(reply: MessageInternal, channel: Channel);

    /**
     * 异常时
     *
     * @param error 异常
     */
    onError(error: Error);
}

/**
 * 流基类
 *
 * @author noear
 * @since 2.0
 */
export abstract class StreamBase implements SteamInternal {
    _sid: string;
    _isSingle: boolean;
    _timeout: number;
    _onError: IoConsumer<Error>;

    constructor(sid: string, isSingle: boolean, timeout: number) {
        this._sid = sid;
        this._isSingle = isSingle;
        this._timeout = timeout;
    }

    abstract onAccept(reply: MessageInternal, channel: Channel);

    abstract isDone(): boolean;

    onError(error: Error) {
        if (this._onError != null) {
            this._onError(error);
        }
    }

    sid(): string {
        return this._sid;
    }

    isSingle(): boolean {
        return this._isSingle;
    }

    timeout(): number {
        return this._timeout;
    }

    thenError(onError: IoConsumer<Error>): Stream {
        this._onError = onError;
        return this;
    }
}

/**
 * 请求流
 *
 * @author noear
 * @since 2.0
 */
export class StreamRequest extends StreamBase implements SteamInternal{
    _future:IoConsumer<Reply>;
    _isDone:boolean;
    constructor( sid:string,  timeout:number,  future:IoConsumer<Reply>) {
        super(sid,false,timeout);
        this._future = future;
        this._isDone = false;
    }

    isDone(): boolean {
        return this._isDone;
    }

    onAccept(reply: MessageInternal, channel: Channel) {
        this._isDone = true;

        try {
            this._future(reply);
        } catch (e) {
            channel.onError(e);
        }
    }
}

/**
 * 订阅流
 *
 * @author noear
 * @since 2.0
 */
export class StreamSubscribe extends StreamBase implements SteamInternal{
    _future:IoConsumer<Reply>;
    constructor( sid:string,  timeout:number,  future:IoConsumer<Reply>) {
        super(sid,false,timeout);
        this._future = future;
    }

    isDone(): boolean {
        return false;
    }

    onAccept(reply: MessageInternal, channel: Channel) {
        try {
            this._future(reply);
        } catch (e) {
            channel.onError(e);
        }
    }
}

export class StreamManger {
    _streamMap: Map<string, SteamInternal>

    constructor() {
        this._streamMap = new Map<string, SteamInternal>();
    }

    getStream(sid) {
        return this._streamMap.get(sid);
    }

    addStream(sid, stream: SteamInternal) {
        this._streamMap.set(sid, stream);
    }

    removeStream(sid) {
        this._streamMap.delete(sid);
    }
}
