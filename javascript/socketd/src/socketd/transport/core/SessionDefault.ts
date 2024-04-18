import {SessionBase} from "./Session";
import type {Channel} from "./Channel";
import type {Handshake} from "./Handshake";
import {Entity, EntityDefault} from "./Entity";
import {Message, MessageBuilder} from "./Message";
import {Frame} from "./Frame";
import {Constants} from "./Constants";
import {Flags} from "./Flags";
import {
    SendStreamImpl,
    RequestStream,
    RequestStreamImpl,
    SubscribeStream,
    SubscribeStreamImpl,
    type SendStream
} from "../stream/Stream";
import {SocketAddress} from "./SocketAddress";

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

    isClosing(): boolean {
        return this._channel.isClosing();
    }

    remoteAddress(): SocketAddress | null {
        return this._channel.getRemoteAddress();
    }

    localAddress(): SocketAddress | null {
        return this._channel.getLocalAddress();
    }

    handshake(): Handshake {
        return this._channel.getHandshake();
    }

    /**
     * 获取握手参数
     *
     * @param name 名字
     */
    param(name: string): string | null {
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
            return this.handshake().path();
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
     *
     * @param event    事件
     * @param entity   实体
     * @return 流
     */
    send(event: string, entity: Entity): SendStream {
        if (entity == null) {
            entity = new EntityDefault();
        }

        const message = new MessageBuilder()
            .sid(this.generateId())
            .event(event)
            .entity(entity)
            .build();

        const stream = new SendStreamImpl(message.sid());
        this._channel.send(new Frame(Flags.Message, message), stream);
        return stream;
    }


    /**
     * 发送并请求（限为一次答复；指定超时）
     *
     * @param event    事件
     * @param entity   实体
     * @param timeout  超时
     * @return 流
     */
    sendAndRequest(event: string, entity: Entity, timeout?: number): RequestStream {
        if (entity == null) {
            entity = new EntityDefault();
        }

        //异步，用 streamTimeout
        const message = new MessageBuilder()
            .sid(this.generateId())
            .event(event)
            .entity(entity)
            .build();

        if (!timeout) {
            timeout = 0;
        }

        if (timeout < 0) {
            timeout = this._channel.getConfig().getStreamTimeout();
        }

        if (timeout == 0) {
            timeout = this._channel.getConfig().getRequestTimeout();
        }

        const stream = new RequestStreamImpl(message.sid(), timeout);
        this._channel.send(new Frame(Flags.Request, message), stream);
        return stream;
    }

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event    事件
     * @param entity   实体
     * @param timeout  超时
     * @return 流
     */
    sendAndSubscribe(event: string, entity: Entity, timeout?: number): SubscribeStream {
        if (entity == null) {
            entity = new EntityDefault();
        }

        const message = new MessageBuilder()
            .sid(this.generateId())
            .event(event)
            .entity(entity)
            .build();

        if (!timeout) {
            timeout = 0;
        }

        if (timeout <= 0) {
            timeout = this._channel.getConfig().getStreamTimeout();
        }

        const stream = new SubscribeStreamImpl(message.sid(), timeout);
        this._channel.send(new Frame(Flags.Subscribe, message), stream);
        return stream;
    }

    /**
     * 答复
     *
     * @param from    来源消息
     * @param entity  实体
     */
    reply(from: Message, entity: Entity) {
        if (entity == null) {
            entity = new EntityDefault();
        }

        const message = new MessageBuilder()
            .sid(from.sid())
            .event(from.event())
            .entity(entity)
            .build();

        this._channel.send(new Frame(Flags.Reply, message), null);
    }

    /**
     * 答复并结束（即最后一次答复）
     *
     * @param from    来源消息
     * @param entity  实体
     */
    replyEnd(from: Message, entity: Entity) {
        if (entity == null) {
            entity = new EntityDefault();
        }

        const message = new MessageBuilder()
            .sid(from.sid())
            .event(from.event())
            .entity(entity)
            .build();

        this._channel.send(new Frame(Flags.ReplyEnd, message), null);
    }

    closeStarting() {
        this.preclose();
    }

    preclose() {
        console.debug(`${this._channel.getConfig().getRoleName()} session close starting, sessionId=${this.sessionId()}`);

        if (this._channel.isValid()) {
            this._channel.sendClose(Constants.CLOSE1000_PROTOCOL_CLOSE_STARTING);
        }
    }

    /**
     * 关闭
     */
    close() {
        console.debug(`${this._channel.getConfig().getRoleName()} session will be closed, sessionId=${this.sessionId()}`);

        if (this._channel.isValid()) {
            try {
                this._channel.sendClose(Constants.CLOSE1001_PROTOCOL_CLOSE);
            } catch (e) {
                console.warn(`${this._channel.getConfig().getRoleName()} channel sendClose error`, e);
            }
        }

        this._channel.close(Constants.CLOSE2009_USER);
    }
}