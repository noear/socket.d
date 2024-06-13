import type {Message} from "../transport/core/Message";

/**
 * 异常
 *
 * @author noear
 * @since 2.0
 */
export class SocketDException extends Error {
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
export class SocketDAlarmException extends SocketDException {
    private _alarm: Message;
    private _alarmCode: number;

    constructor(alarm: Message) {
        super(alarm.entity()!.dataAsString());
        this._alarm = alarm;
        this._alarmCode = alarm.metaAsInt("code");
    }

    getAlarm(): Message {
        return this._alarm;
    }

    getAlarmCode(): number {
        return this._alarmCode;
    }
}

/**
 * 通道异常
 *
 * @author noear
 * @since 2.0
 */
export class SocketDChannelException extends SocketDException {
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
export class SocketDCodecException extends SocketDException {
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
export class SocketDConnectionException extends SocketDException {
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
export class SocketDSizeLimitException extends SocketDException {
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
export class SocketDTimeoutException extends SocketDException {
    constructor(message) {
        super(message);
    }
}