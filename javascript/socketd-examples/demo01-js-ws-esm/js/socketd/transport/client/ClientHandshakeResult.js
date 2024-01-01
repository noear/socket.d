/**
 * 客户端握手结果
 *
 * @author noear
 * @since 2.0
 */
export class ClientHandshakeResult {
    constructor(channel, throwable) {
        this._channel = channel;
        this._throwable = throwable;
    }
    getChannel() {
        return this._channel;
    }
    getThrowable() {
        return this._throwable;
    }
}
