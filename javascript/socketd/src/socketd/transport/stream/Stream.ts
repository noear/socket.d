import {IoConsumer, IoTriConsumer} from "../core/Typealias";
import {Reply} from "../core/Entity";
import {MessageInternal} from "../core/Message";
import {StreamManger} from "./StreamManger";


/**
 * 流
 *
 * @author noear
 * @since 2.1
 */
export interface Stream <T extends Stream<any>> {
    /**
     * 流Id
     */
    sid(): string;

    /**
     * 是否完成
     */
    isDone(): boolean;

    /**
     * 异常发生时
     */
    thenError(onError: IoConsumer<Error>): T;

    /**
     * 进度发生时
     */
    thenProgress(onProgress: IoTriConsumer<boolean, number, number>): T;
}

/**
 * 发送流
 *
 * @author noear
 * @since 2.3
 */
export interface SendStream extends Stream<SendStream>{

}

/**
 * 请求流
 *
 * @author noear
 * @since 2.3
 */
export interface RequestStream extends Stream<RequestStream> {
    /**
     * 异步等待获取答复
     */
    await(): Promise<Reply>;

    /**
     * 答复发生时
     */
    thenReply(onReply: IoConsumer<Reply>): RequestStream;
}

/**
 * 订阅流
 *
 * @author noear
 * @since 2.3
 */
export interface SubscribeStream extends Stream<SubscribeStream> {
    /**
     * 答复发生时
     */
    thenReply(onReply: IoConsumer<Reply>): SubscribeStream;
}



/**
 * 流内部接口
 *
 * @author noear
 * @since 2.0
 */
export interface StreamInternal<T extends Stream<any>> extends Stream<T> {
    /**
     * 获取需求数量（0，1，2）
     */
    demands(): number;

    /**
     * 超时设定（单位：毫秒）
     */
    timeout(): number;

    /**
     * 保险开始（避免永久没有回调，造成内存不能释放）
     * */
    insuranceStart(streamManger: StreamManger, streamTimeout: number);

    /**
     * 保险取消息
     * */
    insuranceCancel();

    /**
     * 接收时
     *
     * @param reply   答复
     */
    onReply(reply: MessageInternal);

    /**
     * 异常时
     *
     * @param error 异常
     */
    onError(error: Error);

    /**
     * 进度时
     *
     * @param isSend 是否为发送
     * @param val 当时值
     * @param max 最大值
     */
    onProgress(isSend: boolean, val: number, max: number);
}