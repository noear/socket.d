import type {Channel} from "./Channel";
import type {Frame} from "./Frame";
import type {MessageInternal} from "./Message";
import {Entity} from "./Entity";
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
