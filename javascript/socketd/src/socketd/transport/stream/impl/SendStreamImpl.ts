import {Constants} from "../../core/Constants";
import {MessageInternal} from "../../core/Message";
import {SendStream} from "../Stream";
import {StreamBase} from "./StreamBase";

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