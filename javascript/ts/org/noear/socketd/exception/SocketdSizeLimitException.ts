/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.exception {
    /**
     * 大小限制异常
     * 
     * @author noear
     * @since 2.0
     * @param {Error} cause
     * @class
     * @extends org.noear.socketd.exception.SocketdException
     */
    export class SocketdSizeLimitException extends org.noear.socketd.exception.SocketdException {
        public constructor(cause?: any) {
            if (((cause != null && (cause["__classes"] && cause["__classes"].indexOf("java.lang.Throwable") >= 0) || cause != null && cause instanceof <any>Error) || cause === null)) {
                let __args = arguments;
                super(cause);
            } else if (((typeof cause === 'string') || cause === null)) {
                let __args = arguments;
                let message: any = __args[0];
                super(message);
            } else throw new Error('invalid overload');
        }
    }
    SocketdSizeLimitException["__class"] = "org.noear.socketd.exception.SocketdSizeLimitException";

}

