/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core.fragment {
    /**
     * 数据分片默认实现（可以重写，把大流先缓存到磁盘以节省内存）
     * 
     * @author noear
     * @since 2.0
     * @class
     */
    export class FragmentHandlerDefault implements org.noear.socketd.transport.core.FragmentHandler {
        /**
         * 获取一个分片
         * @param {*} config
         * @param {java.util.concurrent.atomic.AtomicReference} fragmentIndex
         * @param {*} entity
         * @return {*}
         */
        public nextFragment(config: org.noear.socketd.transport.core.Config, fragmentIndex: java.util.concurrent.atomic.AtomicReference<number>, entity: org.noear.socketd.transport.core.Entity): org.noear.socketd.transport.core.Entity {
            fragmentIndex.set(fragmentIndex.get() + 1);
            const fragmentBuf: java.io.ByteArrayOutputStream = new java.io.ByteArrayOutputStream();
            org.noear.socketd.utils.IoUtils.transferTo(entity.data(), fragmentBuf, 0, org.noear.socketd.transport.core.Config.MAX_SIZE_FRAGMENT);
            const fragmentBytes: number[] = fragmentBuf.toByteArray();
            if (fragmentBytes.length === 0){
                return null;
            }
            const fragmentEntity: org.noear.socketd.transport.core.entity.EntityDefault = new org.noear.socketd.transport.core.entity.EntityDefault().data$byte_A(fragmentBytes);
            if (fragmentIndex.get() === 1){
                fragmentEntity.metaMap$java_util_Map(entity.metaMap());
            }
            fragmentEntity.meta$java_lang_String$java_lang_String(org.noear.socketd.transport.core.EntityMetas.META_DATA_FRAGMENT_IDX, /* valueOf */String(fragmentIndex).toString());
            return fragmentEntity;
        }

        /**
         * 聚合分片（可以重写，把大流先缓存到磁盘以节省内存）
         * @param {*} channel
         * @param {number} index
         * @param {org.noear.socketd.transport.core.Frame} frame
         * @return {org.noear.socketd.transport.core.Frame}
         */
        public aggrFragment(channel: org.noear.socketd.transport.core.Channel, index: number, frame: org.noear.socketd.transport.core.Frame): org.noear.socketd.transport.core.Frame {
            let aggregator: org.noear.socketd.transport.core.fragment.FragmentAggregator = <any>(channel.getAttachment<any>(frame.getMessage().sid()));
            if (aggregator == null){
                aggregator = new org.noear.socketd.transport.core.fragment.FragmentAggregator(frame);
                channel.setAttachment(aggregator.getSid(), aggregator);
            }
            aggregator.add(index, frame);
            if (aggregator.getDataLength() > aggregator.getDataStreamSize()){
                return null;
            } else {
                return aggregator.get();
            }
        }

        constructor() {
        }
    }
    FragmentHandlerDefault["__class"] = "org.noear.socketd.transport.core.fragment.FragmentHandlerDefault";
    FragmentHandlerDefault["__interfaces"] = ["org.noear.socketd.transport.core.FragmentHandler"];


}

