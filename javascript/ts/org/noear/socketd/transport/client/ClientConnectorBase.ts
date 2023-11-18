/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.client {
    /**
     * 客户端连接器基类
     * 
     * @author noear
     * @since 2.0
     * @param {org.noear.socketd.transport.client.ClientBase} client
     * @class
     */
    export abstract class ClientConnectorBase<T extends org.noear.socketd.transport.client.ClientBase<any>> implements org.noear.socketd.transport.client.ClientConnector {
        client: T;

        public constructor(client: T) {
            if (this.client === undefined) { this.client = null; }
            this.client = client;
        }

        /**
         * 心跳处理
         * @return {*}
         */
        public heartbeatHandler(): org.noear.socketd.transport.core.HeartbeatHandler {
            return <any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return (session) =>  (funcInst['heartbeat'] ? funcInst['heartbeat'] : funcInst) .call(funcInst, session)})(this.client.heartbeatHandler$()));
        }

        /**
         * 心跳频率（单位：毫秒）
         * @return {number}
         */
        public heartbeatInterval(): number {
            return this.client.heartbeatInterval();
        }

        /**
         * 是否自动重连
         * @return {boolean}
         */
        public autoReconnect(): boolean {
            return this.client.config$().isAutoReconnect();
        }

        public abstract close(): any;
        public abstract connect(): any;    }
    ClientConnectorBase["__class"] = "org.noear.socketd.transport.client.ClientConnectorBase";
    ClientConnectorBase["__interfaces"] = ["org.noear.socketd.transport.client.ClientConnector"];


}

