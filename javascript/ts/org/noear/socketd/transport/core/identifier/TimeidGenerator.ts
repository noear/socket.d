/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.identifier {
    /**
     * 时间生成器时间适配（不适合分布式）
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export class TimeidGenerator implements org.noear.socketd.transport.core.IdGenerator {
        /*private*/ basetime: number;

        /*private*/ count: java.util.concurrent.atomic.AtomicLong;

        /**
         * 创建
         * @return {string}
         */
        public generate(): string {
            const tmp: number = (n => n<0?Math.ceil(n):Math.floor(n))(/* currentTimeMillis */Date.now() / 1000);
            if (tmp !== this.basetime){
                {
                    this.basetime = tmp;
                    this.count.set(0);
                };
            }
            return /* valueOf */String(this.basetime * 1000 * 1000 * 1000 + this.count.incrementAndGet()).toString();
        }

        constructor() {
            if (this.basetime === undefined) { this.basetime = 0; }
            this.count = new java.util.concurrent.atomic.AtomicLong();
        }
    }
    TimeidGenerator["__class"] = "org.noear.socketd.transport.core.identifier.TimeidGenerator";
    TimeidGenerator["__interfaces"] = ["org.noear.socketd.transport.core.IdGenerator"];


}

