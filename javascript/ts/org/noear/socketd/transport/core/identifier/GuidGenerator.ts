/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.identifier {
    /**
     * Id 生成顺 guid 适配
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export class GuidGenerator implements org.noear.socketd.transport.core.IdGenerator {
        /**
         * 
         * @return {string}
         */
        public generate(): string {
            return /* replaceAll */java.util.UUID.randomUUID().toString().replace(new RegExp("-", 'g'),"");
        }

        constructor() {
        }
    }
    GuidGenerator["__class"] = "org.noear.socketd.transport.core.identifier.GuidGenerator";
    GuidGenerator["__interfaces"] = ["org.noear.socketd.transport.core.IdGenerator"];


}

