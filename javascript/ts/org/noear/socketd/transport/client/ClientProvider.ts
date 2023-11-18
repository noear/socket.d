/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.client {
    /**
     * 客户端工厂
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export interface ClientProvider {
        /**
         * 协议架构
         * @return {java.lang.String[]}
         */
        schemas(): string[];

        /**
         * 创建客户端
         * @param {org.noear.socketd.transport.client.ClientConfig} clientConfig
         * @return {*}
         */
        createClient(clientConfig: org.noear.socketd.transport.client.ClientConfig): org.noear.socketd.transport.client.Client;
    }
}

