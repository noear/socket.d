import type {Reply} from "./Entity";
import type {MessageInternal} from "./Message";
import type {Channel} from "./Channel";
import type {IoConsumer} from "./Typealias";
import {SocketdTimeoutException} from "../../exception/SocketdException";
import type {Config} from "./Config";
import {Asserts} from "./Asserts";

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

/**
 * 流内部接口
 *
 * @author noear
 * @since 2.0
 */
export interface StreamInternal extends Stream {
    /**
     * 保险开始（避免永久没有回调，造成内存不能释放）
     * */
    insuranceStart(streamManger:StreamMangerDefault, streamTimeout:number);

    /**
     * 保险取消息
     * */
    insuranceCancel();

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
export abstract class StreamBase implements StreamInternal {
    //保险任务
    private  _insuranceFuture: number;
    private _sid: string;
    private _isSingle: boolean;
    private _timeout: number;
    private _doOnError: IoConsumer<Error>;

    constructor(sid: string, isSingle: boolean, timeout: number) {
        this._sid = sid;
        this._isSingle = isSingle;
        this._timeout = timeout;
    }

    abstract onAccept(reply: MessageInternal, channel: Channel);

    abstract isDone(): boolean;



    sid(): string {
        return this._sid;
    }

    isSingle(): boolean {
        return this._isSingle;
    }

    timeout(): number {
        return this._timeout;
    }



    /**
     * 保险开始（避免永久没有回调，造成内存不能释放）
     *
     * @param streamManger  流管理器
     * @param streamTimeout 流超时
     */
    insuranceStart(streamManger: StreamMangerDefault, streamTimeout: number) {
        if (this._insuranceFuture > 0) {
            return;
        }

        this._insuranceFuture = window.setTimeout(() => {
            streamManger.removeStream(this.sid());
            this.onError(new SocketdTimeoutException("The stream response timeout, sid=" + this.sid()));
        }, streamTimeout);
    }

    /**
     * 保险取消息
     */
    insuranceCancel() {
        if (this._insuranceFuture > 0) {
            window.clearTimeout(this._insuranceFuture);
        }
    }

    /**
     * 异常时
     *
     * @param error 异常
     */
    onError(error: Error) {
        if (this._doOnError != null) {
            this._doOnError(error);
        }
    }

    thenError(onError: IoConsumer<Error>): Stream {
        this._doOnError = onError;
        return this;
    }
}

/**
 * 请求流
 *
 * @author noear
 * @since 2.0
 */
export class StreamRequest extends StreamBase implements StreamInternal{
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
export class StreamSubscribe extends StreamBase implements StreamInternal{
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

/**
 * 流管理器
 *
 * @author noear
 * @since 2.0
 */
export interface StreamManger {
    /**
     * 添加流
     *
     * @param sid    流Id
     * @param stream 流
     */
    addStream(sid: string, stream: StreamInternal);

    /**
     * 获取流
     *
     * @param sid 流Id
     */
    getStream(sid: string): StreamInternal | undefined;

    /**
     * 移除流
     *
     * @param sid 流Id
     */
    removeStream(sid: string);
}

export class StreamMangerDefault implements StreamManger{
    _config:Config;
    _streamMap: Map<string, StreamInternal>

    constructor(config:Config) {
        this._config = config;
        this._streamMap = new Map<string, StreamInternal>();
    }

    /**
     * 获取流接收器
     *
     * @param sid 流Id
     */
    getStream(sid:string) {
        return this._streamMap.get(sid);
    }

    /**
     * 添加流接收器
     *
     * @param sid    流Id
     * @param stream 流
     */
    addStream(sid, stream: StreamInternal) {
        Asserts.assertNull("stream", stream);

        this._streamMap.set(sid, stream);

        //增加流超时处理（做为后备保险）
        let streamTimeout = stream.timeout() > 0 ? stream.timeout() : this._config.getStreamTimeout();
        if (streamTimeout > 0) {
            stream.insuranceStart(this, streamTimeout);
        }
    }

    /**
     * 移除流接收器
     *
     * @param sid 流Id
     */
    removeStream(sid) {
        let stream = this.getStream(sid);

        if (stream) {
            this._streamMap.delete(sid);
            stream.insuranceCancel();
            console.debug(`${this._config.getRoleName()} stream removed, sid=${sid}`);
        }
    }
}
