import {IoConsumer, IoTriConsumer} from "../../core/Typealias";
import {MessageInternal} from "../../core/Message";
import {SocketDTimeoutException} from "../../../exception/SocketDException";
import {Stream, StreamInternal} from "../Stream";
import {StreamManger} from "../StreamManger";

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
    insuranceStart(streamManger: StreamManger, streamTimeout: number) {
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