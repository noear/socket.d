import type {Channel} from "./Channel";
import type {Frame} from "./Frame";
import type {MessageInternal} from "./Message";
import {Entity, EntityDefault} from "./Entity";
import {EntityMetas} from "./Constants";
import {FragmentAggregator, FragmentAggregatorDefault} from "./FragmentAggregator";
import {CodecReader} from "./Codec";

/**
 * 数据分片处理（分片必须做，聚合可开关）
 *
 * @author noear
 * @since 2.0
 */
export interface FragmentHandler {
    /**
     * 获取下个分片
     *
     * @param channel       通道
     * @param fragmentIndex 分片索引（由导引安排，从1按序递进）
     * @param message       总包消息
     */
    nextFragment(channel: Channel, fragmentIndex: number, message: MessageInternal): Entity;

    /**
     * 聚合所有分片
     *
     * @param channel       通道
     * @param fragmentIndex 分片索引（传过来信息，不一定有顺序）
     * @param message       分片消息
     */
    aggrFragment(channel: Channel, fragmentIndex: number, message: MessageInternal): Frame;

    /**
     * 聚合启用
     */
    aggrEnable(): boolean;
}

/**
 * 数据分片默认实现（可以重写，把大流先缓存到磁盘以节省内存）
 *
 * @author noear
 * @since 2.0
 */
export class FragmentHandlerDefault implements FragmentHandler {
    /**
     * 获取下个分片
     *
     * @param channel       通道
     * @param fragmentIndex 分片索引（由导引安排，从1按序递进）
     * @param message       总包消息
     */
    nextFragment(channel: Channel, fragmentIndex: number, message: MessageInternal): Entity {

        const dataBuffer = this.readFragmentData(message.dataAsReader(), channel.getConfig().getFragmentSize());
        if (dataBuffer == null || dataBuffer.byteLength == 0) {
            return null;
        }

        const fragmentEntity = new EntityDefault().dataSet(dataBuffer);
        if (fragmentIndex == 1) {
            fragmentEntity.metaMapPut(message.metaMap());
        }
        fragmentEntity.metaPut(EntityMetas.META_DATA_FRAGMENT_IDX, fragmentIndex.toString());

        return fragmentEntity;
    }

    /**
     * 聚合所有分片
     *
     * @param channel       通道
     * @param fragmentIndex 分片索引（传过来信息，不一定有顺序）
     * @param message       分片消息
     */
    aggrFragment(channel: Channel, fragmentIndex: number, message: MessageInternal): Frame {
        let aggregator: FragmentAggregator = channel.getAttachment(message.sid());
        if (aggregator == null) {
            aggregator = new FragmentAggregatorDefault(message);
            channel.putAttachment(aggregator.getSid(), aggregator);
        }

        aggregator.add(fragmentIndex, message);

        if (aggregator.getDataLength() > aggregator.getDataStreamSize()) {
            //长度不够，等下一个分片包
            return null;
        } else {
            //重置为聚合帖
            channel.putAttachment(message.sid(), null);
            return aggregator.get();
        }
    }

    aggrEnable(): boolean {
        return true;
    }

    readFragmentData(ins: CodecReader, maxSize: number): ArrayBuffer {
        let size:number;
        if (ins.remaining() > maxSize) {
            size = maxSize;
        } else {
            size = ins.remaining();
        }

        const buf = new ArrayBuffer(size);

        ins.getBytes(buf, 0, size);

        return buf;
    }
}
