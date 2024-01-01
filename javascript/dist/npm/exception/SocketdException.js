"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.SocketdTimeoutException = exports.SocketdSizeLimitException = exports.SocketdConnectionException = exports.SocketdCodecException = exports.SocketdChannelException = exports.SocketdAlarmException = exports.SocketdException = void 0;
/**
 * 异常
 *
 * @author noear
 * @since 2.0
 */
class SocketdException extends Error {
    constructor(message) {
        super(message);
    }
}
exports.SocketdException = SocketdException;
/**
 * 告警异常
 *
 * @author noear
 * @since 2.0
 */
class SocketdAlarmException extends SocketdException {
    constructor(from) {
        super(from.entity().dataAsString());
        this._from = from;
    }
    getFrom() {
        return this._from;
    }
}
exports.SocketdAlarmException = SocketdAlarmException;
/**
 * 通道异常
 *
 * @author noear
 * @since 2.0
 */
class SocketdChannelException extends SocketdException {
    constructor(message) {
        super(message);
    }
}
exports.SocketdChannelException = SocketdChannelException;
/**
 * 编码异常
 *
 * @author noear
 * @since 2.0
 */
class SocketdCodecException extends SocketdException {
    constructor(message) {
        super(message);
    }
}
exports.SocketdCodecException = SocketdCodecException;
/**
 * 连接异常
 *
 * @author noear
 * @since 2.0
 */
class SocketdConnectionException extends SocketdException {
    constructor(message) {
        super(message);
    }
}
exports.SocketdConnectionException = SocketdConnectionException;
/**
 * 大小限制异常
 *
 * @author noear
 * @since 2.0
 */
class SocketdSizeLimitException extends SocketdException {
    constructor(message) {
        super(message);
    }
}
exports.SocketdSizeLimitException = SocketdSizeLimitException;
/**
 * 超时异常
 *
 * @author noear
 * @since 2.0
 */
class SocketdTimeoutException extends SocketdException {
    constructor(message) {
        super(message);
    }
}
exports.SocketdTimeoutException = SocketdTimeoutException;
