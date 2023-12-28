import {ChannelInternal} from "../core/Channel";

/**
 * 客户端握手结果
 *
 * @author noear
 * @since 2.0
 */
export class ClientHandshakeResult {
    private _channel: ChannelInternal;
    private _throwable: Error;

    constructor(channel: ChannelInternal, throwable: Error) {
        this._channel = channel;
        this._throwable = throwable;
    }


    getChannel(): ChannelInternal {
        return this._channel;
    }

    getThrowable(): Error {
        return this._throwable;
    }
}