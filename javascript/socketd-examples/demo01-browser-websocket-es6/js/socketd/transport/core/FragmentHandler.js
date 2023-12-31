import { EntityDefault } from "./Entity";
import { EntityMetas } from "./Constants";
import { FragmentAggregatorDefault } from "./FragmentAggregator";
/**
 * 数据分片默认实现（可以重写，把大流先缓存到磁盘以节省内存）
 *
 * @author noear
 * @since 2.0
 */
export class FragmentHandlerDefault {
    /**
     * 拆割分片
     *
     * @param channel       通道
     * @param message       总包消息
     * @param consumer 分片消费
     */
    spliFragment(channel, message, consumer) {
        if (message.dataSize() > channel.getConfig().getFragmentSize()) {
            let fragmentIndex = 0;
            this.spliFragmentDo(fragmentIndex, channel, message, consumer);
        }
        else {
            if (message.data().getBlob() == null) {
                consumer(message);
            }
            else {
                message.data().getBytes(channel.getConfig().getFragmentSize(), dataBuffer => {
                    consumer(new EntityDefault().dataSet(dataBuffer).metaMapPut(message.metaMap()));
                });
            }
        }
    }
    spliFragmentDo(fragmentIndex, channel, message, consumer) {
        //获取分片
        fragmentIndex++;
        message.data().getBytes(channel.getConfig().getFragmentSize(), dataBuffer => {
            const fragmentEntity = new EntityDefault().dataSet(dataBuffer);
            if (fragmentIndex == 1) {
                fragmentEntity.metaMapPut(message.metaMap());
            }
            fragmentEntity.metaPut(EntityMetas.META_DATA_FRAGMENT_IDX, fragmentIndex.toString());
            consumer(fragmentEntity);
            this.spliFragmentDo(fragmentIndex, channel, message, consumer);
        });
    }
    /**
     * 聚合分片
     *
     * @param channel       通道
     * @param fragmentIndex 分片索引（传过来信息，不一定有顺序）
     * @param message       分片消息
     */
    aggrFragment(channel, fragmentIndex, message) {
        let aggregator = channel.getAttachment(message.sid());
        if (!aggregator) {
            aggregator = new FragmentAggregatorDefault(message);
            channel.putAttachment(aggregator.getSid(), aggregator);
        }
        aggregator.add(fragmentIndex, message);
        if (aggregator.getDataLength() > aggregator.getDataStreamSize()) {
            //长度不够，等下一个分片包
            return null;
        }
        else {
            //重置为聚合帖
            channel.putAttachment(message.sid(), null);
            return aggregator.get();
        }
    }
    aggrEnable() {
        return true;
    }
}
