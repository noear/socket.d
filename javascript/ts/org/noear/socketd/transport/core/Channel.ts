import {Config} from "./Config";
import {Message} from "./Message";
import {Frame} from "./Frame";
import {Acceptor} from "./Acceptor";
import {IoConsumer} from "../../utils/Functions";
import {HandshakeInternal} from "./HandshakeInternal";
import {Session} from "./Session";

/**
 * 通道
 *
 * @author noear
 * @since 2.0
 * @class
 */
export interface Channel {
    /**
     * 获取附件
     * @param {string} name
     * @return {*}
     */
    getAttachment<T>(name: string): T;

    /**
     * 设置附件
     * @param {string} name
     * @param {*} val
     */
    setAttachment(name: string, val: any);

    /**
     * 移除接收器（答复接收器）
     * @param {string} sid
     */
    removeAcceptor(sid: string);

    /**
     * 是否有效
     * @return {boolean}
     */
    isValid(): boolean;

    /**
     * 是否已关闭
     * @return {number}
     */
    isClosed(): number;

    /**
     * 关闭（1协议关，2用户关）
     *
     * @param {number} code
     */
    close(code: number);

    /**
     * 获取配置
     * @return {*}
     */
    getConfig(): Config;

    /**
     * 获取请求计数（用于背压控制）
     * @return {java.util.concurrent.atomic.AtomicInteger}
     */
    getRequests(): number;

    /**
     * 设置握手信息
     *
     * @param {HandshakeInternal} handshake 握手信息
     */
    setHandshake(handshake: HandshakeInternal);

    /**
     * 获取握手信息
     * @return {HandshakeInternal}
     */
    getHandshake(): HandshakeInternal;

    /**
     * 获取远程地址
     * @return {java.net.InetSocketAddress}
     */
    getRemoteAddress(): string;

    /**
     * 获取本地地址
     * @return {java.net.InetSocketAddress}
     */
    getLocalAddress(): string;

    /**
     * 发送连接（握手）
     *
     * @param {string} url 连接地址
     */
    sendConnect(url: string);

    /**
     * 发送连接确认（握手）
     *
     * @param {*} connectMessage 连接消息
     */
    sendConnack(connectMessage: Message);

    /**
     * 发送 Ping（心跳）
     */
    sendPing();

    /**
     * 发送 Pong（心跳）
     */
    sendPong();

    /**
     * 发送 Close
     */
    sendClose();

    /**
     * 发送
     *
     * @param {Frame} frame    帧
     * @param {*} acceptor 答复接收器（没有则为 null）
     */
    send(frame: Frame, acceptor: Acceptor);

    /**
     * 接收（接收答复帧）
     *
     * @param {Frame} frame 帧
     * @param {*} onError
     */
    retrieve(frame: Frame, onError: IoConsumer<Error>);

    /**
     * 手动重连（一般是自动）
     */
    reconnect();

    /**
     * 获取会话
     * @return {*}
     */
    getSession(): Session;
}