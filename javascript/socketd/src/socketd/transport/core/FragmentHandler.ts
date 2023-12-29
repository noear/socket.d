import {Channel} from "./Channel";
import {Frame} from "./Frame";
import {MessageInternal} from "./Message";
import {Entity} from "./Entity";

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
    nextFragment(channel: Channel, fragmentIndex: number, message: MessageInternal): Entity {
        throw new Error("Method not implemented.");
    }

    aggrFragment(channel: Channel, fragmentIndex: number, message: MessageInternal): Frame {
        throw new Error("Method not implemented.");
    }

    aggrEnable(): boolean {
        return false;
    }
}
