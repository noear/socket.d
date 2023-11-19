/**
 * 异常
 *
 * @author noear
 * @since 2.0
 * @param {Error} cause
 * @class
 * @extends Error
 */
export class SocketdException extends Error {
    message: string
    cause?: any
    constructor({message, cause}: { message: string, cause?: any }) {
        super();
        this.message = message;
        this.cause = cause;
    }
}

/**
 * 通道异常
 *
 * @author noear
 * @since 2.0
 * @param {Error} cause
 * @class
 * @extends SocketdException
 */
export class SocketdChannelException extends SocketdException {
    constructor({message, cause}: { message: string, cause?: any }) {
        super({message, cause});
    }
}

/**
 * 编码异常
 *
 * @author noear
 * @since 2.0
 * @param {Error} cause
 * @class
 * @extends SocketdException
 */
export class SocketdCodecException extends SocketdException {
    constructor({message, cause}: { message: string, cause?: any }) {
        super({message, cause});
    }
}

/**
 * 连接异常
 *
 * @author noear
 * @since 2.0
 * @param {Error} cause
 * @class
 * @extends SocketdException
 */
export class SocketdConnectionException extends SocketdException {
    constructor({message, cause}: { message: string, cause?: any }) {
        super({message, cause});
    }
}

/**
 * 大小限制异常
 *
 * @author noear
 * @since 2.0
 * @param {Error} cause
 * @class
 * @extends SocketdException
 */
export class SocketdSizeLimitException extends SocketdException {
    constructor({message, cause}: { message: string, cause?: any }) {
        super({message, cause});
    }
}

/**
 * 超时异常
 *
 * @author noear
 * @since 2.0
 * @param {Error} cause
 * @class
 * @extends SocketdException
 */
export class SocketdTimeoutException extends SocketdException {
    constructor({message, cause}: { message: string, cause?: any }) {
        super({message, cause});
    }
}