/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.fragment {
    /**
     * 分片聚合器
     * 
     * @author noear
     * @since 2.0
     * @param {org.noear.socketd.transport.core.Frame} main
     * @class
     */
    export class FragmentAggregator {
        /*private*/ main: org.noear.socketd.transport.core.Frame;

        /*private*/ fragmentHolders: Array<org.noear.socketd.transport.core.fragment.FragmentHolder>;

        /*private*/ dataStreamSize: number;

        /*private*/ dataLength: number;

        public constructor(main: org.noear.socketd.transport.core.Frame) {
            if (this.main === undefined) { this.main = null; }
            this.fragmentHolders = <any>([]);
            if (this.dataStreamSize === undefined) { this.dataStreamSize = 0; }
            if (this.dataLength === undefined) { this.dataLength = 0; }
            this.main = main;
            const dataLengthStr: string = main.getMessage().meta(org.noear.socketd.transport.core.EntityMetas.META_DATA_LENGTH);
            if (org.noear.socketd.utils.Utils.isEmpty$java_lang_String(dataLengthStr)){
                throw new org.noear.socketd.exception.SocketdCodecException("Missing \'" + org.noear.socketd.transport.core.EntityMetas.META_DATA_LENGTH + "\' meta, topic=" + main.getMessage().topic());
            }
            this.dataLength = /* parseInt */parseInt(dataLengthStr);
        }

        /**
         * 获取消息流Id（用于消息交互、分片）
         * @return {string}
         */
        public getSid(): string {
            return this.main.getMessage().sid();
        }

        /**
         * 数据流大小
         * @return {number}
         */
        public getDataStreamSize(): number {
            return this.dataStreamSize;
        }

        /**
         * 数据总长度
         * @return {number}
         */
        public getDataLength(): number {
            return this.dataLength;
        }

        /**
         * 获取聚合后的帧
         * @return {org.noear.socketd.transport.core.Frame}
         */
        public get(): org.noear.socketd.transport.core.Frame {
            this.fragmentHolders.sort(<any>(((funcInst: any) => { if (typeof funcInst == 'function') { return funcInst } return (arg0, arg1) =>  (funcInst['compare'] ? funcInst['compare'] : funcInst) .call(funcInst, arg0, arg1)})(java.util.Comparator<any, any>((fh) => fh.getIndex()))));
            const dataStream: java.io.ByteArrayOutputStream = new java.io.ByteArrayOutputStream(this.dataLength);
            for(let index = 0; index < this.fragmentHolders.length; index++) {
                let fh = this.fragmentHolders[index];
                {
                    org.noear.socketd.utils.IoUtils.transferTo(fh.getFrame().getMessage().data(), dataStream);
                }
            }
            const inputStream: java.io.ByteArrayInputStream = new java.io.ByteArrayInputStream(dataStream.toByteArray());
            return new org.noear.socketd.transport.core.Frame(this.main.getFlag(), new org.noear.socketd.transport.core.internal.MessageDefault().flag(this.main.getFlag()).sid$java_lang_String(this.main.getMessage().sid()).topic$java_lang_String(this.main.getMessage().topic()).entity$org_noear_socketd_transport_core_Entity(new org.noear.socketd.transport.core.entity.EntityDefault().metaMap$java_util_Map(this.main.getMessage().metaMap()).data$java_io_InputStream(inputStream)));
        }

        /**
         * 添加帧
         * @param {number} index
         * @param {org.noear.socketd.transport.core.Frame} frame
         */
        public add(index: number, frame: org.noear.socketd.transport.core.Frame) {
            /* add */(this.fragmentHolders.push(new org.noear.socketd.transport.core.fragment.FragmentHolder(index, frame))>0);
            this.dataStreamSize = this.dataStreamSize + frame.getMessage().dataSize();
        }
    }
    FragmentAggregator["__class"] = "org.noear.socketd.transport.core.fragment.FragmentAggregator";

}

