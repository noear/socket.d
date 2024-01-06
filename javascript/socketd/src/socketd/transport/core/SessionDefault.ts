import {SessionBase} from "./Session";
import type {Channel} from "./Channel";
import type {Handshake} from "./Handshake";
import type {Entity} from "./Entity";
import {Message, MessageBuilder} from "./Message";
import {Frame} from "./Frame";
import {Constants, Flags} from "./Constants";
import {
    StreamSendImpl,
    StreamRequest,
    StreamRequestImpl,
    StreamSubscribe,
    StreamSubscribeImpl,
    type StreamSend
} from "./Stream";
import * as repl from "repl";

/**
 * 会话默认实现
 *
 * @author noear
 * @since 2.0
 */
export class SessionDefault extends SessionBase {
    private _pathNew: string;

    constructor(channel: Channel) {
        super(channel);
    }

    isValid(): boolean {
        return this._channel.isValid();
    }

    handshake(): Handshake {
        return this._channel.getHandshake();
    }

    /**
     * 获取握手参数
     *
     * @param name 名字
     */
    param(name: string): string | undefined {
        return this.handshake().param(name);
    }

    /**
     * 获取握手参数或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    paramOrDefault(name: string, def: string): string {
        return this.handshake().paramOrDefault(name, def);
    }

    /**
     * 获取路径
     */
    path(): string {
        if (this._pathNew == null) {
            return this.handshake().uri().pathname;
        } else {
            return this._pathNew;
        }
    }

    /**
     * 设置新路径
     */
    pathNew(pathNew: string) {
        this._pathNew = pathNew;
    }

    /**
     * 手动重连（一般是自动）
     */
    reconnect() {
        this._channel.reconnect();
    }

    /**
     * 手动发送 Ping（一般是自动）
     */
    sendPing() {
        this._channel.sendPing();
    }

    sendAlarm(from: Message, alarm: string) {
        this._channel.sendAlarm(from, alarm);
    }

    /**
     * 发送
     */
    send(event: string, content: Entity): StreamSend {
        const message = new MessageBuilder()
            .sid(this.generateId())
            .event(event)
            .entity(content)
            .build();

        const stream = new StreamSendImpl(message.sid());
        new Promise(resolve => {
            this._channel.send(new Frame(Flags.Message, message), stream);
            resolve(1);
        });
        return stream;
    }


    /**
     * 发送并请求（限为一次答复；指定超时）
     *
     * @param event    事件
     * @param content  内容
     * @param timeout 超时
     */
    sendAndRequest(event: string, content: Entity, timeout?: number): StreamRequest {
        //异步，用 streamTimeout
        const message = new MessageBuilder()
            .sid(this.generateId())
            .event(event)
            .entity(content)
            .build();

        if (!timeout) {
            timeout = 0;
        }

        if (timeout < 10) {
            timeout = this._channel.getConfig().getRequestTimeout();
        }

        const stream = new StreamRequestImpl(message.sid(), timeout);
        new Promise(resolve => {
            this._channel.send(new Frame(Flags.Request, message), stream);
            resolve(1);
        });
        return stream;
    }

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event    事件
     * @param content  内容
     * @param timeout 超时
     */
    sendAndSubscribe(event: string, content: Entity, timeout?: number): StreamSubscribe {
        const message = new MessageBuilder()
            .sid(this.generateId())
            .event(event)
            .entity(content)
            .build();

        if (!timeout) {
            timeout = 0;
        }

        if (timeout < 10) {
            timeout = this._channel.getConfig().getStreamTimeout();
        }

        const stream = new StreamSubscribeImpl(message.sid(), timeout);
        new Promise(resolve => {
            this._channel.send(new Frame(Flags.Subscribe, message), stream);
            resolve(1);
        });
        return stream;
    }

    /**
     * 答复
     *
     * @param from    来源消息
     * @param content 内容
     */
    reply(from: Message, content: Entity) {
        const message = new MessageBuilder()
            .sid(from.sid())
            .event(from.event())
            .entity(content)
            .build();

        this._channel.send(new Frame(Flags.Reply, message), null);
    }

    /**
     * 答复并结束（即最后一次答复）
     *
     * @param from    来源消息
     * @param content 内容
     */
    replyEnd(from: Message, content: Entity) {
        const message = new MessageBuilder()
            .sid(from.sid())
            .event(from.event())
            .entity(content)
            .build();

        this._channel.send(new Frame(Flags.ReplyEnd, message), null);
    }

    /**
     * 关闭
     */
    close() {
        console.debug(`${this._channel.getConfig().getRoleName()} session will be closed, sessionId=${this.sessionId()}`);

        if (this._channel.isValid()) {
            try {
                this._channel.sendClose();
            } catch (e) {
                console.warn(`${this._channel.getConfig().getRoleName()} channel sendClose error`, e);
            }
        }

        this._channel.close(Constants.CLOSE4_USER);
    }
}