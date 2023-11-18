/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.client {
    /**
     * 客户端握手结果
     * 
     * @author noear
     * @since 2.0
     * @param {*} channel
     * @param {Error} exception
     * @class
     */
    export class ClientHandshakeResult {
        /*private*/ channel: org.noear.socketd.transport.core.ChannelInternal;

        /*private*/ exception: Error;

        public getChannel(): org.noear.socketd.transport.core.ChannelInternal {
            return this.channel;
        }

        public getException(): Error {
            return this.exception;
        }

        public constructor(channel: org.noear.socketd.transport.core.ChannelInternal, exception: Error) {
            if (this.channel === undefined) { this.channel = null; }
            if (this.exception === undefined) { this.exception = null; }
            this.channel = channel;
            this.exception = exception;
        }
    }
    ClientHandshakeResult["__class"] = "org.noear.socketd.transport.client.ClientHandshakeResult";

}

