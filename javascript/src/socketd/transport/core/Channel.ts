import {Session} from "./Session";
import {Config} from "./Config";
import {HandshakeInternal} from "./HandshakeInternal";
import {Message} from "./Message";
import {Frame} from "./Frame";
import {Acceptor} from "./Acceptor";
import {Consumer} from "../../utils/Consumer";

/**
 * 通道
 *
 * @author noear
 * @since 2.0
 */
export interface Channel {
    /**
     * 获取附件
     */
    getAttachment<T>(name: string): T

    /**
     * 设置附件
     */
    setAttachment(name: string, val: object): void

    /**
     * 移除接收器（答复接收器）
     */
    removeAcceptor(sid: string);

    /**
     * 是否有效
     */
    isClosed(): number;

    /**
     * 关闭（1协议关，2用户关）
     * */
    close(code: number): void

    /**
     * 获取配置
     */
    getConfig(): Config

    /**
     * 获取请求计数（用于背压控制）
     */
    getRequests(): number

    /**
     * 设置握手信息
     *
     * @param handshake 握手信息
     */
    setHandshake(handshake: HandshakeInternal): void

    /**
     * 获取握手信息
     */
    getHandshake(): object

    /**
     * 发送连接（握手）
     *
     * @param url 连接地址
     */
    sendConnect(url: string): void

    /**
     * 发送连接确认（握手）
     *
     * @param connectMessage 连接消息
     */
    sendConnack(connectMessage: Message): void

    /**
     * 发送 Ping（心跳）
     */
    sendPing(): void

    /**
     * 发送 Pong（心跳）
     */
    sendPong(): void

    /**
     * 发送 Close
     */
    sendClose(): void

    /**
     * 发送
     *
     * @param frame    帧
     * @param acceptor 答复接收器（没有则为 null）
     */
    send(frame: Frame, acceptor: Acceptor): void

    /**
     * 接收（接收答复帧）
     *
     * @param frame 帧
     */
    retrieve(frame: Frame, onError: Consumer<Error>): void

    /**
     * 手动重连（一般是自动）
     */
    reconnect(): void

    /**
     * 获取会话
     */
    getSession(): Session
}
