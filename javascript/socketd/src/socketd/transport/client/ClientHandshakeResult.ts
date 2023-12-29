import type {ChannelInternal} from "../core/Channel";

/**
 * 客户端握手结果
 *
 * @author noear
 * @since 2.0
 */
export class ClientHandshakeResult {
    private _channel: ChannelInternal;
    private _throwable: any;

    constructor(channel: ChannelInternal, throwable: any) {
        this._channel = channel;
        this._throwable = throwable;
    }


    getChannel(): ChannelInternal {
        return this._channel;
    }

    getThrowable(): any{
        return this._throwable;
    }
}