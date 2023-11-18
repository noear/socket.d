/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.internal {
    /**
     * 握手信息内部实现类
     * 
     * @author noear
     * @since 2.0
     * @param {*} source
     * @class
     */
    export class HandshakeInternal implements org.noear.socketd.transport.core.Handshake {
        /*private*/ source: org.noear.socketd.transport.core.Message;

        /*private*/ __uri: java.net.URI;

        /*private*/ __version: string;

        /*private*/ __paramMap: any;

        /**
         * 消息源
         * @return {*}
         */
        public getSource(): org.noear.socketd.transport.core.Message {
            return this.source;
        }

        public constructor(source: org.noear.socketd.transport.core.Message) {
            if (this.source === undefined) { this.source = null; }
            if (this.__uri === undefined) { this.__uri = null; }
            if (this.__version === undefined) { this.__version = null; }
            if (this.__paramMap === undefined) { this.__paramMap = null; }
            this.source = source;
            this.__uri = java.net.URI.create(source.topic());
            this.__version = source.meta(org.noear.socketd.transport.core.EntityMetas.META_SOCKETD_VERSION);
            this.__paramMap = <any>({});
            const queryString: string = this.__uri.getQuery();
            if (org.noear.socketd.utils.Utils.isNotEmpty$java_lang_String(queryString)){
                {
                    let array = queryString.split("&");
                    for(let index = 0; index < array.length; index++) {
                        let kvStr = array[index];
                        {
                            const idx: number = kvStr.indexOf('=');
                            if (idx > 0){
                                /* put */(this.__paramMap[kvStr.substring(0, idx)] = kvStr.substring(idx + 1));
                            }
                        }
                    }
                }
            }
        }

        /**
         * 版本
         * @return {string}
         */
        public version(): string {
            return this.__version;
        }

        /**
         * 获请地址
         * 
         * @return {java.net.URI} tcp://192.168.0.1/path?user=1&path=2
         */
        public uri(): java.net.URI {
            return this.__uri;
        }

        /**
         * 获取参数集合
         * @return {*}
         */
        public paramMap(): any {
            return this.__paramMap;
        }

        public param$java_lang_String(name: string): string {
            return /* get */((m,k) => m[k]===undefined?null:m[k])(this.__paramMap, name);
        }

        /**
         * 获取参数或默认值
         * 
         * @param {string} name 名字
         * @param {string} def  默认值
         * @return {string}
         */
        public paramOrDefault(name: string, def: string): string {
            return this.__paramMap.getOrDefault(name, def);
        }

        public param$java_lang_String$java_lang_String(name: string, value: string) {
            /* put */(this.__paramMap[name] = value);
        }

        /**
         * 设置或修改参数
         * 
         * @param {string} name  名字
         * @param {string} value 值
         */
        public param(name?: any, value?: any) {
            if (((typeof name === 'string') || name === null) && ((typeof value === 'string') || value === null)) {
                return <any>this.param$java_lang_String$java_lang_String(name, value);
            } else if (((typeof name === 'string') || name === null) && value === undefined) {
                return <any>this.param$java_lang_String(name);
            } else throw new Error('invalid overload');
        }
    }
    HandshakeInternal["__class"] = "org.noear.socketd.transport.core.internal.HandshakeInternal";
    HandshakeInternal["__interfaces"] = ["org.noear.socketd.transport.core.Handshake"];


}

