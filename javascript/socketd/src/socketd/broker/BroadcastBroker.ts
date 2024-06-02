import {Entity} from "../transport/core/Entity";

/**
 * 广播经纪人
 *
 * @author noear
 * @since 2.4
 */
export interface BroadcastBroker {
    /**
     * 广播
     *
     * @param event  事件
     * @param entity 实体（转发方式 https://socketd.noear.org/article/737 ）
     */
    broadcast(event: string, entity: Entity);
}