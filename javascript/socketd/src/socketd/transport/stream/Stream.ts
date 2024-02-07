import {IoConsumer, IoTriConsumer} from "../core/Typealias";
import {Reply} from "../core/Entity";
import {MessageInternal} from "../core/Message";
import {SocketDTimeoutException} from "../../exception/SocketDException";
import {Config} from "../core/Config";
import {Asserts} from "../core/Asserts";
import {Constants} from "../core/Constants";


/**
 * 流
 *
 * @author noear
 * @since 2.1
 */
export interface Stream <T extends Stream<any>> {
    /**
     * 流Id
     */
    sid(): string;

    /**
     * 是否完成
     */
    isDone(): boolean;

    /**
     * 异常发生时
     */
    thenError(onError: IoConsumer<Error>): T;

    /**
     * 进度发生时
     */
    thenProgress(onProgress: IoTriConsumer<boolean, number, number>): T;
}

/**
 * 发送流
 *
 * @author noear
 * @since 2.3
 */
export interface SendStream extends Stream<SendStream>{

}

/**
 * 请求流
 *
 * @author noear
 * @since 2.3
 */
export interface RequestStream extends Stream<RequestStream> {
    /**
     * 异步等待获取答复
     */
    await(): Promise<Reply>;

    /**
     * 答复发生时
     */
    thenReply(onReply: IoConsumer<Reply>): RequestStream;
}

/**
 * 订阅流
 *
 * @author noear
 * @since 2.3
 */
export interface SubscribeStream extends Stream<SubscribeStream> {
    /**
     * 答复发生时
     */
    thenReply(onReply: IoConsumer<Reply>): SubscribeStream;
}



/**
 * 流内部接口
 *
 * @author noear
 * @since 2.0
 */
export interface StreamInternal<T extends Stream<any>> extends Stream<T> {
    /**
     * 获取需求数量（0，1，2）
     */
    demands(): number;

    /**
     * 超时设定（单位：毫秒）
     */
    timeout(): number;

    /**
     * 保险开始（避免永久没有回调，造成内存不能释放）
     * */
    insuranceStart(streamManger: StreamMangerDefault, streamTimeout: number);

    /**
     * 保险取消息
     * */
    insuranceCancel();

    /**
     * 接收时
     *
     * @param reply   答复
     */
    onReply(reply: MessageInternal);

    /**
     * 异常时
     *
     * @param error 异常
     */
    onError(error: Error);

    /**
     * 进度时
     *
     * @param isSend 是否为发送
     * @param val 当时值
     * @param max 最大值
     */
    onProgress(isSend: boolean, val: number, max: number);
}

/**
 * 流基类
 *
 * @author noear
 * @since 2.0
 */
export abstract class StreamBase<T extends Stream<any>> implements StreamInternal<T> {
    //保险任务
    private _insuranceFuture: any;
    private _sid: string;
    private _demands: number;
    private _timeout: number;
    private _doOnError: IoConsumer<Error>;
    private _doOnProgress: IoTriConsumer<boolean, number, number>;

    constructor(sid: string, demands: number, timeout: number) {
        this._sid = sid;
        this._demands = demands;
        this._timeout = timeout;
    }

    abstract onReply(reply: MessageInternal);

    abstract isDone(): boolean;


    sid(): string {
        return this._sid;
    }

    demands(): number {
        return this._demands;
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
        if (this._insuranceFuture) {
            return;
        }

        this._insuranceFuture = setTimeout(() => {
            streamManger.removeStream(this.sid());
            this.onError(new SocketDTimeoutException("The stream response timeout, sid=" + this.sid()));
        }, streamTimeout);
    }

    /**
     * 保险取消息
     */
    insuranceCancel() {
        if (this._insuranceFuture) {
            clearTimeout(this._insuranceFuture);
        }
    }

    /**
     * 异常时
     *
     * @param error 异常
     */
    onError(error: any) {
        if (this._doOnError) {
            this._doOnError(error);
        }
    }

    onProgress(isSend: boolean, val: number, max: number) {
        if (this._doOnProgress) {
            this._doOnProgress(isSend, val, max);
        }
    }

    thenError(onError: IoConsumer<Error>): any {
        this._doOnError = onError;
        return this;
    }

    thenProgress(onProgress: IoTriConsumer<boolean, number, number>): any {
        this._doOnProgress = onProgress;
        return this;
    }
}

export class SendStreamImpl extends StreamBase<SendStream> implements SendStream{
    constructor(sid: string) {
        super(sid, Constants.DEMANDS_ZERO, 0);
    }

    isDone(): boolean {
        return true;
    }

    onReply(reply: MessageInternal) {
    }
}

/**
 * 请求流
 *
 * @author noear
 * @since 2.0
 */
export class RequestStreamImpl extends StreamBase<RequestStream> implements RequestStream{
    _doOnReply:IoConsumer<Reply>;
    _isDone:boolean;
    constructor(sid:string,  timeout:number) {
        super(sid, Constants.DEMANDS_SIGNLE, timeout);
        this._isDone = false;
    }

    isDone(): boolean {
        return this._isDone;
    }

    onReply(reply: MessageInternal) {
        this._isDone = true;

        try {
            if(this._doOnReply){
                this._doOnReply(reply);
            }
        } catch (e) {
            this.onError(e);
        }
    }

    await(): Promise<Reply> {
        return new Promise<Reply>((resolve, reject) => {
            this.thenReply(reply => {
                resolve(reply);
            }).thenError((err) => {
                reject(err);
            })
        })
    }

    thenReply(onReply: IoConsumer<Reply>): RequestStream {
        this._doOnReply = onReply;
        return this;
    }
}

/**
 * 订阅流
 *
 * @author noear
 * @since 2.0
 */
export class SubscribeStreamImpl extends StreamBase<SubscribeStream> implements SubscribeStream{
    _doOnReply:IoConsumer<Reply>;
    _isDone:boolean;
    constructor(sid:string,  timeout:number) {
        super(sid, Constants.DEMANDS_MULTIPLE, timeout);
        this._isDone = false;
    }

    isDone(): boolean {
        return this._isDone;
    }

    onReply(reply: MessageInternal) {
        this._isDone = reply.isEnd();

        try {
            if(this._doOnReply){
                this._doOnReply(reply);
            }
        } catch (e) {
            this.onError(e);
        }
    }

    thenReply(onReply: IoConsumer<Reply>): SubscribeStream {
        this._doOnReply = onReply;
        return this;
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
    addStream(sid: string, stream: StreamInternal<any>);

    /**
     * 获取流
     *
     * @param sid 流Id
     */
    getStream(sid: string): StreamInternal<any> | null;

    /**
     * 移除流
     *
     * @param sid 流Id
     */
    removeStream(sid: string);
}

export class StreamMangerDefault implements StreamManger{
    _config:Config;
    _streamMap: Map<string, StreamInternal<any>>

    constructor(config:Config) {
        this._config = config;
        this._streamMap = new Map<string, StreamInternal<any>>();
    }

    /**
     * 获取流接收器
     *
     * @param sid 流Id
     */
    getStream(sid:string) {
        const tmp = this._streamMap.get(sid);
        if (tmp) {
            return tmp;
        } else {
            return null;
        }
    }

    /**
     * 添加流接收器
     *
     * @param sid    流Id
     * @param stream 流
     */
    addStream(sid, stream: StreamInternal<any>) {
        Asserts.assertNull("stream", stream);

        if(stream.demands() == Constants.DEMANDS_ZERO){
            //零需求，则不添加
            return;
        }

        this._streamMap.set(sid, stream);

        //增加流超时处理（做为后备保险）
        const streamTimeout = stream.timeout() > 0 ? stream.timeout() : this._config.getStreamTimeout();
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
        const stream = this.getStream(sid);

        if (stream) {
            this._streamMap.delete(sid);
            stream.insuranceCancel();
            console.debug(`${this._config.getRoleName()} stream removed, sid=${sid}`);
        }
    }
}
