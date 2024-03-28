import type {Channel} from "./Channel";
import type {Frame} from "./Frame";
import type {MessageInternal} from "./Message";
import {Entity, EntityDefault} from "./Entity";
import {EntityMetas} from "./EntityMetas";
import {FragmentAggregator, FragmentAggregatorDefault} from "./FragmentAggregator";
import type {IoConsumer} from "./Typealias";
import {StreamInternal} from "../stream/Stream";

/**
 * 数据分片处理（分片必须做，聚合可开关）
 *
 * @author noear
 * @since 2.0
 */
export interface FragmentHandler {
    /**
     * 拆割分片
     *
     * @param channel  通道
     * @param stream   流
     * @param message  总包消息
     * @param consumer 分片消费
     */
    spliFragment(channel: Channel, stream: StreamInternal<any>|null, message: MessageInternal, consumer: IoConsumer<Entity>);

    /**
     * 聚合分片
     *
     * @param channel       通道
     * @param fragmentIndex 分片索引（传过来信息，不一定有顺序）
     * @param message       分片消息
     */
    aggrFragment(channel: Channel, fragmentIndex: number, message: MessageInternal): Frame | null;

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
     * 拆割分片
     *
     * @param channel       通道
     * @param message       总包消息
     * @param consumer 分片消费
     */
    spliFragment(channel: Channel, stream: StreamInternal<any>|null, message: MessageInternal, consumer: IoConsumer<Entity>) {
        if (message.dataSize() > channel.getConfig().getFragmentSize()) {
            let fragmentIndex = 0;
            let fragmentTotal = Math.ceil(message.dataSize() / channel.getConfig().getFragmentSize());

            this.spliFragmentDo(fragmentIndex, fragmentTotal, channel, stream, message, consumer);
        } else {
            if (message.data().getBlob() == null) {
                consumer(message);

                if (stream != null) {
                    stream.onProgress(true, 1, 1);
                }
            } else {
                message.data().getBytes(channel.getConfig().getFragmentSize(), dataBuffer => {
                    consumer(new EntityDefault().dataSet(dataBuffer).metaMapPut(message.metaMap()));

                    if (stream != null) {
                        stream.onProgress(true, 1, 1);
                    }
                });
            }
        }
    }

    spliFragmentDo(fragmentIndex: number, fragmentTotal:number,channel: Channel, stream: StreamInternal<any>|null, message: MessageInternal, consumer: IoConsumer<Entity>) {
        //获取分片
        fragmentIndex++;

        message.data().getBytes(channel.getConfig().getFragmentSize(), dataBuffer => {
            const fragmentEntity = new EntityDefault().dataSet(dataBuffer);
            if (fragmentIndex == 1) {
                fragmentEntity.metaMapPut(message.metaMap());
            }
            fragmentEntity.metaPut(EntityMetas.META_DATA_FRAGMENT_IDX, fragmentIndex.toString());
            fragmentEntity.metaPut(EntityMetas.META_DATA_FRAGMENT_TOTAL, fragmentTotal.toString());

            consumer(fragmentEntity);
            if (stream != null) {
                stream.onProgress(true, fragmentIndex, fragmentTotal);
            }

            this.spliFragmentDo(fragmentIndex, fragmentTotal, channel, stream, message, consumer);
        });
    }

    /**
     * 聚合分片
     *
     * @param channel       通道
     * @param fragmentIndex 分片索引（传过来信息，不一定有顺序）
     * @param message       分片消息
     */
    aggrFragment(channel: Channel, fragmentIndex: number, message: MessageInternal): Frame | null {
        let aggregator: FragmentAggregator | null = channel.getAttachment(message.sid());
        if (!aggregator) {
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
}
