import type {Entity} from "../core/Entity";
import {RequestStream, SendStream, SubscribeStream} from "../stream/Stream";

/**
 * 客户会话
 *
 * @author noear
 */
export interface ClientSession {
    /**
     * 是否有效
     */
    isValid(): boolean;

    /**
     * 是否活跃
     */
    isActive(): boolean;

    /**
     * 是否正在关闭中
     * */
    isClosing(): boolean;

    /**
     * 获取会话Id
     */
    sessionId(): string;

    /**
     * 手动重连（一般是自动）
     */
    reconnect();

    /**
     * 发送
     *
     * @param event   事件
     * @param entity  实体
     * @return 流
     */
    send(event: string, entity: Entity): SendStream;

    /**
     * 发送并请求（限为一次答复；指定回调）
     *
     * @param event    事件
     * @param entity   实体
     * @param timeout  超时（毫秒）
     * @return 流
     */
    sendAndRequest(event: string, entity: Entity, timeout?: number): RequestStream;

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event    事件
     * @param entity   实体
     * @param timeout  超时（毫秒）
     * @return 流
     */
    sendAndSubscribe(event: string, entity: Entity, timeout?: number): SubscribeStream;

    /**
     * 关闭开始
     */
    closeStarting();

    /**
     * 预关闭
     * */
    preclose();

    /**
     * 关闭
     * */
    close();
}
