import {Entity} from "./Entity";
import {Channel} from "./Channel";
import {Frame} from "./Frame";
import {MessageInternal} from "./MessageInternal";

/**
 * 数据分片处理
 *
 * @author noear
 * @since 2.0
 * @class
 */
export interface FragmentHandler {
    /**
     * 获取下个分片
     */
    nextFragment(channel: Channel, fragmentIndex: number, message: MessageInternal): Entity;

    /**
     * 聚合所有分片
     */
    aggrFragment(channel: Channel, fragmentIndex: number, message: MessageInternal): Frame;
}