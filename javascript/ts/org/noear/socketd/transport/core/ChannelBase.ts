/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core {
    /**
     * 通道基类
     * 
     * @author noear
     * @since 2.0
     * @param {*} config
     * @class
     */
    export abstract class ChannelBase implements org.noear.socketd.transport.core.Channel {
        /*private*/ config: org.noear.socketd.transport.core.Config;

        /*private*/ requests: java.util.concurrent.atomic.AtomicInteger;

        /*private*/ attachments: any;

        /*private*/ handshake: org.noear.socketd.transport.core.internal.HandshakeInternal;

        /*private*/ __isClosed: number;

        public getConfig(): org.noear.socketd.transport.core.Config {
            return this.config;
        }

        public constructor(config: org.noear.socketd.transport.core.Config) {
            if (this.config === undefined) { this.config = null; }
            this.requests = new java.util.concurrent.atomic.AtomicInteger();
            this.attachments = <any>(new java.util.concurrent.ConcurrentHashMap<any, any>());
            if (this.handshake === undefined) { this.handshake = null; }
            if (this.__isClosed === undefined) { this.__isClosed = 0; }
            this.config = config;
        }

        /**
         * 
         * @param {string} name
         * @return {*}
         */
        public getAttachment<T>(name: string): T {
            return <T><any>/* get */((m,k) => m[k]===undefined?null:m[k])(this.attachments, name);
        }

        /**
         * 
         * @param {string} name
         * @param {*} val
         */
        public setAttachment(name: string, val: any) {
            /* put */(this.attachments[name] = val);
        }

        /**
         * 
         * @return {number}
         */
        public isClosed(): number {
            return this.__isClosed;
        }

        /**
         * 
         * @param {number} code
         */
        public close(code: number) {
            this.__isClosed = code;
            /* clear */(obj => { for (let member in obj) delete obj[member]; })(this.attachments);
        }

        /**
         * 
         * @return {java.util.concurrent.atomic.AtomicInteger}
         */
        public getRequests(): java.util.concurrent.atomic.AtomicInteger {
            return this.requests;
        }

        /**
         * 
         * @param {org.noear.socketd.transport.core.internal.HandshakeInternal} handshake
         */
        public setHandshake(handshake: org.noear.socketd.transport.core.internal.HandshakeInternal) {
            this.handshake = handshake;
        }

        /**
         * 
         * @return {org.noear.socketd.transport.core.internal.HandshakeInternal}
         */
        public getHandshake(): org.noear.socketd.transport.core.internal.HandshakeInternal {
            return this.handshake;
        }

        /**
         * 
         * @param {string} uri
         */
        public sendConnect(uri: string) {
            this.send(org.noear.socketd.transport.core.internal.Frames.connectFrame(this.getConfig().getIdGenerator().generate(), uri), null);
        }

        /**
         * 
         * @param {*} connectMessage
         */
        public sendConnack(connectMessage: org.noear.socketd.transport.core.Message) {
            this.send(org.noear.socketd.transport.core.internal.Frames.connackFrame(connectMessage), null);
        }

        /**
         * 
         */
        public sendPing() {
            this.send(org.noear.socketd.transport.core.internal.Frames.pingFrame(), null);
        }

        /**
         * 
         */
        public sendPong() {
            this.send(org.noear.socketd.transport.core.internal.Frames.pongFrame(), null);
        }

        /**
         * 
         */
        public sendClose() {
            this.send(org.noear.socketd.transport.core.internal.Frames.closeFrame(), null);
        }

        public abstract getLocalAddress(): any;
        public abstract getRemoteAddress(): any;
        public abstract getSession(): any;
        public abstract isValid(): any;
        public abstract reconnect(): any;
        public abstract removeAcceptor(sid?: any): any;
        public abstract retrieve(frame?: any, onError?: any): any;
        public abstract send(frame?: any, acceptor?: any): any;    }
    ChannelBase["__class"] = "org.noear.socketd.transport.core.ChannelBase";
    ChannelBase["__interfaces"] = ["org.noear.socketd.transport.core.Channel"];


}

