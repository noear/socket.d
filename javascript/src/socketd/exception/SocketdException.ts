export class SocketdException extends Error {
    message: string
    cause?: any

    constructor({message, cause}: { message: string, cause?: any }) {
        super();
        this.message = message;
        this.cause = cause;
    }
}