import type {Message} from "../transport/core/Message";

/**
 * 异常
 *
 * @author noear
 * @since 2.0
 */
export class SocketdException extends Error {
    constructor(message) {
        super(message);
    }
}

/**
 * 告警异常
 *
 * @author noear
 * @since 2.0
 */
export class SocketdAlarmException extends SocketdException {
    private  _from: Message;

    constructor(from: Message) {
        super(from.entity()!.dataAsString());
        this._from = from;
    }

    getFrom(): Message {
        return this._from;
    }
}

/**
 * 通道异常
 *
 * @author noear
 * @since 2.0
 */
export class SocketdChannelException extends SocketdException {
    constructor(message) {
        super(message);
    }
}

/**
 * 编码异常
 *
 * @author noear
 * @since 2.0
 */
export class SocketdCodecException extends SocketdException {
    constructor(message) {
        super(message);
    }
}

/**
 * 连接异常
 *
 * @author noear
 * @since 2.0
 */
export class SocketdConnectionException extends SocketdException {
    constructor(message) {
        super(message);
    }
}

/**
 * 大小限制异常
 *
 * @author noear
 * @since 2.0
 */
export class SocketdSizeLimitException extends SocketdException {
    constructor(message) {
        super(message);
    }
}


/**
 * 超时异常
 *
 * @author noear
 * @since 2.0
 */
export class SocketdTimeoutException extends SocketdException {
    constructor(message) {
        super(message);
    }
}