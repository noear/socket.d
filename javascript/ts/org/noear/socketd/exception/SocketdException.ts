/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.exception {
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
        public constructor(cause?: any) {
            if (((cause != null && (cause["__classes"] && cause["__classes"].indexOf("java.lang.Throwable") >= 0) || cause != null && cause instanceof <any>Error) || cause === null)) {
                let __args = arguments;
                super(cause); this.message=cause;
            } else if (((typeof cause === 'string') || cause === null)) {
                let __args = arguments;
                let message: any = __args[0];
                super(message); this.message=message;
            } else throw new Error('invalid overload');
        }
    }
    SocketdException["__class"] = "org.noear.socketd.exception.SocketdException";

}

