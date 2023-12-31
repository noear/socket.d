"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ClientHandshakeResult = void 0;
/**
 * 客户端握手结果
 *
 * @author noear
 * @since 2.0
 */
class ClientHandshakeResult {
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
exports.ClientHandshakeResult = ClientHandshakeResult;
