import {IoConsumer} from "../../core/Typealias";
import {Reply} from "../../core/Entity";
import {Constants} from "../../core/Constants";
import {MessageInternal} from "../../core/Message";
import {SubscribeStream} from "../Stream";
import {StreamBase} from "./StreamBase";

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