/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.server {
    /**
     * 服务端基类
     * 
     * @author noear
     * @since 2.0
     * @param {org.noear.socketd.transport.server.ServerConfig} config
     * @param {*} assistant
     * @class
     */
    export abstract class ServerBase<T extends org.noear.socketd.transport.core.ChannelAssistant<any>> implements org.noear.socketd.transport.server.Server {
        /*private*/ __processor: org.noear.socketd.transport.core.Processor;

        /*private*/ __config: org.noear.socketd.transport.server.ServerConfig;

        /*private*/ __assistant: T;

        isStarted: boolean;

        public constructor(config: org.noear.socketd.transport.server.ServerConfig, assistant: T) {
            this.__processor = new org.noear.socketd.transport.core.internal.ProcessorDefault();
            if (this.__config === undefined) { this.__config = null; }
            if (this.__assistant === undefined) { this.__assistant = null; }
            if (this.isStarted === undefined) { this.isStarted = false; }
            this.__config = config;
            this.__assistant = assistant;
        }

        /**
         * 获取通道助理
         * @return {*}
         */
        public assistant(): T {
            return this.__assistant;
        }

        public config$(): org.noear.socketd.transport.server.ServerConfig {
            return this.__config;
        }

        public config$org_noear_socketd_transport_server_ServerConfigHandler(consumer: org.noear.socketd.transport.server.ServerConfigHandler): org.noear.socketd.transport.server.Server {
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
                return <any>this.config$org_noear_socketd_transport_server_ServerConfigHandler(consumer);
            } else if (consumer === undefined) {
                return <any>this.config$();
            } else throw new Error('invalid overload');
        }

        /**
         * 获取处理器
         * @return {*}
         */
        public processor(): org.noear.socketd.transport.core.Processor {
            return this.__processor;
        }

        /**
         * 设置处理器
         * @param {*} processor
         * @return {*}
         */
        public process(processor: org.noear.socketd.transport.core.Processor): org.noear.socketd.transport.server.Server {
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
        public listen(listener: org.noear.socketd.transport.core.Listener): org.noear.socketd.transport.server.Server {
            this.__processor.setListener(listener);
            return this;
        }

        public abstract start(): any;
        public abstract stop(): any;
        public abstract title(): any;    }
    ServerBase["__class"] = "org.noear.socketd.transport.server.ServerBase";
    ServerBase["__interfaces"] = ["org.noear.socketd.transport.server.Server"];


}

