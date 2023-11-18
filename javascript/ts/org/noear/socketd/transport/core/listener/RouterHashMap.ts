/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.listener {
    /**
     * 哈希由器
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export class RouterHashMap implements org.noear.socketd.transport.core.listener.Router {
        /*private*/ table: any;

        /**
         * 
         * @param {string} path
         * @return {*}
         */
        public matching(path: string): org.noear.socketd.transport.core.Listener {
            return /* get */((m,k) => m[k]===undefined?null:m[k])(this.table, path);
        }

        /**
         * 
         * @param {string} path
         */
        public remove(path: string) {
            /* remove */(map => { let deleted = this.table[path];delete this.table[path];return deleted;})(this.table);
        }

        /**
         * 
         * @return {number}
         */
        public count(): number {
            return /* size */Object.keys(this.table).length;
        }

        /**
         * 
         * @param {string} path
         * @param {*} listener
         */
        public add(path: string, listener: org.noear.socketd.transport.core.Listener) {
            /* put */(this.table[path] = listener);
        }

        constructor() {
            this.table = <any>({});
        }
    }
    RouterHashMap["__class"] = "org.noear.socketd.transport.core.listener.RouterHashMap";
    RouterHashMap["__interfaces"] = ["org.noear.socketd.transport.core.listener.Router"];


}

