import {IoConsumer} from "../../core/Typealias";
import {Reply} from "../../core/Entity";
import {Constants} from "../../core/Constants";
import {MessageInternal} from "../../core/Message";
import {RequestStream} from "../Stream";
import {StreamBase} from "./StreamBase";

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