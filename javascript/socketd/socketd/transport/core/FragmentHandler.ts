import {Channel} from "./Channel";
import {Entity, Frame, MessageInternal} from "./Message";

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

export class FragmentHandlerDefault implements FragmentHandler {
    nextFragment(channel: Channel, fragmentIndex: number, message: MessageInternal): Entity {
        throw new Error("Method not implemented.");
    }

    aggrFragment(channel: Channel, fragmentIndex: number, message: MessageInternal): Frame {
        throw new Error("Method not implemented.");
    }

    aggrEnable(): boolean {
        throw new Error("Method not implemented.");
    }
}
