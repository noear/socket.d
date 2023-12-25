import {Message} from "../transport/core/Message";

export class SocketdException extends Error {
    constructor(message) {
        super(message);
    }
}


export class SocketdAlarmException extends SocketdException {
    _from: Message;

    constructor(from: Message) {
        super(from.entity().dataAsString());
        this._from = from;
    }

    getFrom(): Message {
        return this._from;
    }
}

export class SocketdChannelException extends SocketdException {
    constructor(message) {
        super(message);
    }
}

export class SocketdCodecException extends SocketdException {
    constructor(message) {
        super(message);
    }
}

export class SocketdConnectionException extends SocketdException {
    constructor(message) {
        super(message);
    }
}

export class SocketdSizeLimitException extends SocketdException {
    constructor(message) {
        super(message);
    }
}

export class SocketdTimeoutException extends SocketdException {
    constructor(message) {
        super(message);
    }
}