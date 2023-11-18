/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd {
    /**
     * @author noear
     * @since 2.0
     * @class
     */
    export class SocketD {
        static __static_initialized: boolean = false;
        static __static_initialize() { if (!SocketD.__static_initialized) { SocketD.__static_initialized = true; SocketD.__static_initializer_0(); } }

        /**
         * 版本版本号
         * @return {string}
         */
        public static version(): string {
            return "2.0";
        }

        static clientProviderMap: any; public static clientProviderMap_$LI$(): any { SocketD.__static_initialize();  return SocketD.clientProviderMap; }

        static serverProviderMap: any; public static serverProviderMap_$LI$(): any { SocketD.__static_initialize();  return SocketD.serverProviderMap; }

        static  __static_initializer_0() {
            SocketD.clientProviderMap = <any>({});
            SocketD.serverProviderMap = <any>({});
            java.util.ServiceLoader.load<any>("org.noear.socketd.transport.client.ClientProvider").iterator().forEachRemaining((factory) => {
                {
                    let array = factory.schemas();
                    for(let index = 0; index < array.length; index++) {
                        let s = array[index];
                        {
                            /* put */(SocketD.clientProviderMap_$LI$()[s] = factory);
                        }
                    }
                }
            });
            java.util.ServiceLoader.load<any>("org.noear.socketd.transport.server.ServerProvider").iterator().forEachRemaining((factory) => {
                {
                    let array = factory.schemas();
                    for(let index = 0; index < array.length; index++) {
                        let s = array[index];
                        {
                            /* put */(SocketD.serverProviderMap_$LI$()[s] = factory);
                        }
                    }
                }
            });
        }

        /**
         * 创建服务端
         * @param {string} schema
         * @return {*}
         */
        public static createServer(schema: string): org.noear.socketd.transport.server.Server {
            const server: org.noear.socketd.transport.server.Server = SocketD.createServerOrNull(schema);
            if (server == null){
                throw Object.defineProperty(new Error("No socketd server providers were found."), '__classes', { configurable: true, value: ['java.lang.Throwable','java.lang.IllegalStateException','java.lang.Object','java.lang.RuntimeException','java.lang.Exception'] });
            } else {
                return server;
            }
        }

        /**
         * 创建服务端，如果没有则为 null
         * @param {string} schema
         * @return {*}
         */
        public static createServerOrNull(schema: string): org.noear.socketd.transport.server.Server {
            org.noear.socketd.transport.core.Asserts.assertNull(schema, "schema");
            const factory: org.noear.socketd.transport.server.ServerProvider = /* get */((m,k) => m[k]===undefined?null:m[k])(SocketD.serverProviderMap_$LI$(), schema);
            if (factory == null){
                return null;
            } else {
                return factory.createServer(new org.noear.socketd.transport.server.ServerConfig(schema));
            }
        }

        /**
         * 创建客户端（支持 url 自动识别）
         * 
         * @param {string} serverUrl 服务器地址
         * @return {*}
         */
        public static createClient(serverUrl: string): org.noear.socketd.transport.client.Client {
            const client: org.noear.socketd.transport.client.Client = SocketD.createClientOrNull(serverUrl);
            if (client == null){
                throw Object.defineProperty(new Error("No socketd client providers were found."), '__classes', { configurable: true, value: ['java.lang.Throwable','java.lang.IllegalStateException','java.lang.Object','java.lang.RuntimeException','java.lang.Exception'] });
            } else {
                return client;
            }
        }

        /**
         * 创建客户端（支持 url 自动识别），如果没有则为 null
         * 
         * @param {string} serverUrl 服务器地址
         * @return {*}
         */
        public static createClientOrNull(serverUrl: string): org.noear.socketd.transport.client.Client {
            org.noear.socketd.transport.core.Asserts.assertNull(serverUrl, "serverUrl");
            const idx: number = serverUrl.indexOf("://");
            if (idx < 2){
                throw Object.defineProperty(new Error("The serverUrl invalid: " + serverUrl), '__classes', { configurable: true, value: ['java.lang.Throwable','java.lang.Object','java.lang.RuntimeException','java.lang.IllegalArgumentException','java.lang.Exception'] });
            }
            const schema: string = serverUrl.substring(0, idx);
            const factory: org.noear.socketd.transport.client.ClientProvider = /* get */((m,k) => m[k]===undefined?null:m[k])(SocketD.clientProviderMap_$LI$(), schema);
            if (factory == null){
                return null;
            } else {
                const clientConfig: org.noear.socketd.transport.client.ClientConfig = new org.noear.socketd.transport.client.ClientConfig(serverUrl);
                return factory.createClient(clientConfig);
            }
        }
    }
    SocketD["__class"] = "org.noear.socketd.SocketD";

}


org.noear.socketd.SocketD.__static_initialize();
