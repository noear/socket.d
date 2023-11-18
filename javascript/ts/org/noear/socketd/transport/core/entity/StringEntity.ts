/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.entity {
    /**
     * 字符串实体
     * 
     * @author noear
     * @since 2.0
     * @param {string} str
     * @class
     * @extends org.noear.socketd.transport.core.entity.EntityDefault
     */
    export class StringEntity extends org.noear.socketd.transport.core.entity.EntityDefault {
        public constructor(str: string) {
            super();
            this.data$byte_A(/* getBytes */(str).split('').map(s => s.charCodeAt(0)));
        }
    }
    StringEntity["__class"] = "org.noear.socketd.transport.core.entity.StringEntity";
    StringEntity["__interfaces"] = ["org.noear.socketd.transport.core.Entity"];


}

