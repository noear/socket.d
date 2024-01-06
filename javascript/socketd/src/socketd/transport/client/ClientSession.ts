import type {Entity} from "../core/Entity";
import {RequestStream, SendStream, SubscribeStream} from "../stream/Stream";
import {IoConsumer} from "../core/Typealias";

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
     * @param content 内容
     */
    send(event: string, content: Entity, consumer?: IoConsumer<SendStream>): SendStream;

    /**
     * 发送并请求（限为一次答复；指定回调）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     * @param timeout  超时（毫秒）
     * @return 流
     */
    sendAndRequest(event: string, content: Entity, timeout?: number, consumer?: IoConsumer<RequestStream>): RequestStream;

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     * @param timeout  超时（毫秒）
     * @return 流
     */
    sendAndSubscribe(event: string, content: Entity, timeout?: number, consumer?: IoConsumer<SubscribeStream>): SubscribeStream;

    /**
     * 关闭
     * */
    close();
}
