/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.client {
    /**
     * 客记端配置（单位：毫秒）
     * 
     * @author noear
     * @since 2.0
     * @param {string} url
     * @class
     * @extends org.noear.socketd.transport.core.ConfigBase
     */
    export class ClientConfig extends org.noear.socketd.transport.core.ConfigBase<ClientConfig> {
        /*private*/ schema: string;

        /*private*/ url: string;

        /*private*/ uri: java.net.URI;

        /*private*/ port: number;

        /*private*/ __heartbeatInterval: number;

        /*private*/ __connectTimeout: number;

        /*private*/ __autoReconnect: boolean;

        public constructor(url: string) {
            super(true);
            if (this.schema === undefined) { this.schema = null; }
            if (this.url === undefined) { this.url = null; }
            if (this.uri === undefined) { this.uri = null; }
            if (this.port === undefined) { this.port = 0; }
            if (this.__heartbeatInterval === undefined) { this.__heartbeatInterval = 0; }
            if (this.__connectTimeout === undefined) { this.__connectTimeout = 0; }
            if (this.__autoReconnect === undefined) { this.__autoReconnect = false; }
            if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(url, "sd:")){
                url = url.substring(3);
            }
            this.url = url;
            this.uri = java.net.URI.create(url);
            this.port = this.uri.getPort();
            this.schema = this.uri.getScheme();
            if (this.port < 0){
                this.port = 8602;
            }
            this.__connectTimeout = 10000;
            this.__heartbeatInterval = 20000;
            this.__autoReconnect = true;
        }

        /**
         * 获取通讯架构（tcp, ws, udp）
         * @return {string}
         */
        public getSchema(): string {
            return this.schema;
        }

        /**
         * 获取连接地址
         * @return {string}
         */
        public getUrl(): string {
            return this.url;
        }

        /**
         * 获取连接地址
         * @return {java.net.URI}
         */
        public getUri(): java.net.URI {
            return this.uri;
        }

        /**
         * 获取连接主机
         * @return {string}
         */
        public getHost(): string {
            return this.uri.getHost();
        }

        /**
         * 获取连接端口
         * @return {number}
         */
        public getPort(): number {
            return this.port;
        }

        /**
         * 获取心跳间隔（单位毫秒）
         * @return {number}
         */
        public getHeartbeatInterval(): number {
            return this.__heartbeatInterval;
        }

        /**
         * 配置心跳间隔（单位毫秒）
         * @param {number} heartbeatInterval
         * @return {org.noear.socketd.transport.client.ClientConfig}
         */
        public heartbeatInterval(heartbeatInterval: number): ClientConfig {
            this.__heartbeatInterval = heartbeatInterval;
            return this;
        }

        /**
         * 获取连接超时（单位毫秒）
         * @return {number}
         */
        public getConnectTimeout(): number {
            return this.__connectTimeout;
        }

        /**
         * 配置连接超时（单位毫秒）
         * @param {number} connectTimeout
         * @return {org.noear.socketd.transport.client.ClientConfig}
         */
        public connectTimeout(connectTimeout: number): ClientConfig {
            this.__connectTimeout = connectTimeout;
            return this;
        }

        /**
         * 获取是否自动重链
         * @return {boolean}
         */
        public isAutoReconnect(): boolean {
            return this.__autoReconnect;
        }

        /**
         * 配置是否自动重链
         * @param {boolean} autoReconnect
         * @return {org.noear.socketd.transport.client.ClientConfig}
         */
        public autoReconnect(autoReconnect: boolean): ClientConfig {
            this.__autoReconnect = autoReconnect;
            return this;
        }

        /**
         * 
         * @param {number} idleTimeout
         * @return {org.noear.socketd.transport.client.ClientConfig}
         */
        public idleTimeout(idleTimeout: number): ClientConfig {
            if (this.__autoReconnect === false){
                return super.idleTimeout(idleTimeout);
            } else {
                return super.idleTimeout(0);
            }
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return "ClientConfig{schema=\'" + this.schema + '\'' + ", charset=" + this.__charset + ", url=\'" + this.url + '\'' + ", heartbeatInterval=" + this.__heartbeatInterval + ", connectTimeout=" + this.__connectTimeout + ", idleTimeout=" + this.__idleTimeout + ", requestTimeout=" + this.__requestTimeout + ", readBufferSize=" + this.__readBufferSize + ", writeBufferSize=" + this.__writeBufferSize + ", autoReconnect=" + this.__autoReconnect + ", maxRequests=" + this.__maxRequests + ", maxUdpSize=" + this.__maxUdpSize + '}';
        }
    }
    ClientConfig["__class"] = "org.noear.socketd.transport.client.ClientConfig";
    ClientConfig["__interfaces"] = ["org.noear.socketd.transport.core.Config"];


}

