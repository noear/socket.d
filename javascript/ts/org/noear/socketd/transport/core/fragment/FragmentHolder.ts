/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.fragment {
    /**
     * 分片持有人
     * 
     * @author noear
     * @since 2.0
     * @param {number} index
     * @param {org.noear.socketd.transport.core.Frame} frame
     * @class
     */
    export class FragmentHolder {
        /*private*/ index: number;

        /*private*/ frame: org.noear.socketd.transport.core.Frame;

        public constructor(index: number, frame: org.noear.socketd.transport.core.Frame) {
            if (this.index === undefined) { this.index = 0; }
            if (this.frame === undefined) { this.frame = null; }
            this.index = index;
            this.frame = frame;
        }

        /**
         * 获取顺序位
         * @return {number}
         */
        public getIndex(): number {
            return this.index;
        }

        /**
         * 获取分片帧
         * @return {org.noear.socketd.transport.core.Frame}
         */
        public getFrame(): org.noear.socketd.transport.core.Frame {
            return this.frame;
        }
    }
    FragmentHolder["__class"] = "org.noear.socketd.transport.core.fragment.FragmentHolder";

}

