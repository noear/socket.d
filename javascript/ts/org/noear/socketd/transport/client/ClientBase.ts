/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.client {
    /**
     * 客户端基类
     * 
     * @author noear
     * @since 2.0
     * @param {org.noear.socketd.transport.client.ClientConfig} clientConfig
     * @param {*} assistant
     * @class
     */
    export abstract class ClientBase<T extends org.noear.socketd.transport.core.ChannelAssistant<any>> implements org.noear.socketd.transport.client.Client {
        __processor: org.noear.socketd.transport.core.Processor;

        __heartbeatHandler: org.noear.socketd.transport.core.HeartbeatHandler;

        /*private*/ __config: org.noear.socketd.transport.client.ClientConfig;

        /*private*/ __assistant: T;

        public constructor(clientConfig: org.noear.socketd.transport.client.ClientConfig, assistant: T) {
            this.__processor = new org.noear.socketd.transport.core.internal.ProcessorDefault();
            if (this.__heartbeatHandler === undefined) { this.__heartbeatHandler = null; }
            if (this.__config === undefined) { this.__config = null; }
            if (this.__assistant === undefined) { this.__assistant = null; }
            this.__config = clientConfig;
            this.__assistant = assistant;
        }

        /**
         * 获取通道助理
         * @return {*}
         */
        public assistant(): T {
            return this.__assistant;
        }

        public heartbeatHandler$(): org.noear.socketd.transport.core.HeartbeatHandler {
            return <any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return (session) =>  (funcInst['heartbeat'] ? funcInst['heartbeat'] : funcInst) .call(funcInst, session)})(this.__heartbeatHandler));
        }

        /**
         * 获取心跳间隔（毫秒）
         * @return {number}
         */
        public heartbeatInterval(): number {
            return this.__config.getHeartbeatInterval();
        }

        public config$(): org.noear.socketd.transport.client.ClientConfig {
            return this.__config;
        }

        /**
         * 获取处理器
         * @return {*}
         */
        public processor(): org.noear.socketd.transport.core.Processor {
            return this.__processor;
        }

        public heartbeatHandler$org_noear_socketd_transport_core_HeartbeatHandler(handler: org.noear.socketd.transport.core.HeartbeatHandler): org.noear.socketd.transport.client.Client {
            if (handler != null){
                this.__heartbeatHandler = <any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return (session) =>  (funcInst['heartbeat'] ? funcInst['heartbeat'] : funcInst) .call(funcInst, session)})(handler));
            }
            return this;
        }

        /**
         * 设置心跳
         * @param {*} handler
         * @return {*}
         */
        public heartbeatHandler(handler?: any): any {
            if (((typeof handler === 'function' && (<any>handler).length === 1) || handler === null)) {
                return <any>this.heartbeatHandler$org_noear_socketd_transport_core_HeartbeatHandler(handler);
            } else if (handler === undefined) {
                return <any>this.heartbeatHandler$();
            } else throw new Error('invalid overload');
        }

        public config$org_noear_socketd_transport_client_ClientConfigHandler(consumer: org.noear.socketd.transport.client.ClientConfigHandler): org.noear.socketd.transport.client.Client {
            consumer(this.__config);
            return this;
        }

        /**
         * 配置
         * @param {*} consumer
         * @return {*}
         */
        public config(consumer?: any): any {
            if (((typeof consumer === 'function' && (<any>consumer).length === 1) || consumer === null)) {
                return <any>this.config$org_noear_socketd_transport_client_ClientConfigHandler(consumer);
            } else if (consumer === undefined) {
                return <any>this.config$();
            } else throw new Error('invalid overload');
        }

        /**
         * 设置处理器
         * @param {*} processor
         * @return {*}
         */
        public process(processor: org.noear.socketd.transport.core.Processor): org.noear.socketd.transport.client.Client {
            if (processor != null){
                this.__processor = processor;
            }
            return this;
        }

        /**
         * 设置监听器
         * @param {*} listener
         * @return {*}
         */
        public listen(listener: org.noear.socketd.transport.core.Listener): org.noear.socketd.transport.client.Client {
            this.__processor.setListener(listener);
            return this;
        }

        /**
         * 打开会话
         * @return {*}
         */
        public open(): org.noear.socketd.transport.core.Session {
            const connector: org.noear.socketd.transport.client.ClientConnector = this.createConnector();
            const channel0: org.noear.socketd.transport.core.ChannelInternal = connector.connect();
            const clientChannel: org.noear.socketd.transport.client.ClientChannel = new org.noear.socketd.transport.client.ClientChannel(channel0, connector);
            clientChannel.setHandshake(channel0.getHandshake());
            const session: org.noear.socketd.transport.core.Session = new org.noear.socketd.transport.core.internal.SessionDefault(clientChannel);
            channel0.setSession(session);
            return session;
        }

        /**
         * 创建连接器
         * @return {*}
         */
        abstract createConnector(): org.noear.socketd.transport.client.ClientConnector;
    }
    ClientBase["__class"] = "org.noear.socketd.transport.client.ClientBase";
    ClientBase["__interfaces"] = ["org.noear.socketd.transport.client.Client"];


}

