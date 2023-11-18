/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.server {
    /**
     * 服务端属性（单位：毫秒）
     * 
     * @author noear
     * @since 2.0
     * @param {string} schema
     * @class
     * @extends org.noear.socketd.transport.core.ConfigBase
     */
    export class ServerConfig extends org.noear.socketd.transport.core.ConfigBase<ServerConfig> {
        /*private*/ schema: string;

        /*private*/ __host: string;

        /*private*/ __port: number;

        public constructor(schema: string) {
            super(false);
            if (this.schema === undefined) { this.schema = null; }
            if (this.__host === undefined) { this.__host = null; }
            if (this.__port === undefined) { this.__port = 0; }
            if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(schema, "sd:")){
                schema = schema.substring(3);
            }
            this.schema = schema;
            this.__host = "";
            this.__port = 8602;
        }

        /**
         * 获取协议架构
         * @return {string}
         */
        public getSchema(): string {
            return this.schema;
        }

        /**
         * 获取主机
         * @return {string}
         */
        public getHost(): string {
            return this.__host;
        }

        /**
         * 配置主机
         * @param {string} host
         * @return {org.noear.socketd.transport.server.ServerConfig}
         */
        public host(host: string): ServerConfig {
            this.__host = host;
            return this;
        }

        /**
         * 获取端口
         * @return {number}
         */
        public getPort(): number {
            return this.__port;
        }

        /**
         * 配置端口
         * @param {number} port
         * @return {org.noear.socketd.transport.server.ServerConfig}
         */
        public port(port: number): ServerConfig {
            this.__port = port;
            return this;
        }

        /**
         * 获取本机地址
         * @return {string}
         */
        public getLocalUrl(): string {
            if (org.noear.socketd.utils.Utils.isEmpty$java_lang_String(this.__host)){
                return this.schema + "://127.0.0.1:" + this.__port;
            } else {
                return this.schema + "://" + this.__host + ":" + this.__port;
            }
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return "ServerConfig{schema=\'" + this.schema + '\'' + ", charset=" + this.__charset + ", host=\'" + this.__host + '\'' + ", port=" + this.__port + ", coreThreads=" + this.__coreThreads + ", maxThreads=" + this.__maxThreads + ", idleTimeout=" + this.__idleTimeout + ", replyTimeout=" + this.__requestTimeout + ", readBufferSize=" + this.__readBufferSize + ", writeBufferSize=" + this.__writeBufferSize + ", maxRequests=" + this.__maxRequests + ", maxUdpSize=" + this.__maxUdpSize + '}';
        }
    }
    ServerConfig["__class"] = "org.noear.socketd.transport.server.ServerConfig";
    ServerConfig["__interfaces"] = ["org.noear.socketd.transport.core.Config"];


}

