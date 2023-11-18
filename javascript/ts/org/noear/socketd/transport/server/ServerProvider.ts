/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.server {
    /**
     * 服务端工厂
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export interface ServerProvider {
        /**
         * 协议架构
         * @return {java.lang.String[]}
         */
        schemas(): string[];

        /**
         * 创建服务端
         * @param {org.noear.socketd.transport.server.ServerConfig} serverConfig
         * @return {*}
         */
        createServer(serverConfig: org.noear.socketd.transport.server.ServerConfig): org.noear.socketd.transport.server.Server;
    }
}

