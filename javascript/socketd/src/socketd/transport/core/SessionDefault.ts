import {SessionBase} from "./Session";
import {Channel} from "./Channel";
import {Handshake} from "./Handshake";
import {Entity, Frame, Message, MessageDefault, Reply} from "./Message";
import {Constants, Flags} from "./Constants";
import {IoConsumer} from "./Types";
import {Stream, StreamRequest, StreamSubscribe} from "./Stream";

export class SessionDefault extends SessionBase {
    _pathNew: string;

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
    param(name: string): string {
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
    send(event: string, content: Entity) {
        let message = new MessageDefault(Flags.Message, this.generateId(), event, content);

        this._channel.send(new Frame(Flags.Message, message), null);
    }


    /**
     * 发送并请求（限为一次答复；指定超时）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     * @param timeout 超时
     */
    sendAndRequest(event: string, content: Entity, consumer: IoConsumer<Reply>, timeout: number): Stream {
        //异步，用 streamTimeout
        let message = new MessageDefault(Flags.Request, this.generateId(), event, content);

        let stream = new StreamRequest(message.sid(), timeout, consumer);
        this._channel.send(new Frame(Flags.Request, message), stream);
        return stream;
    }

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     * @param timeout 超时
     */
    sendAndSubscribe(event: string, content: Entity, consumer: IoConsumer<Reply>, timeout: number): Stream {
        let message = new MessageDefault(Flags.Subscribe, this.generateId(), event, content);
        let stream = new StreamSubscribe(message.sid(), timeout, consumer);
        this._channel.send(new Frame(Flags.Subscribe, message), stream);
        return stream;
    }

    /**
     * 答复
     *
     * @param from    来源消息
     * @param content 内容
     */
    reply(from: Message, content: Entity) {
        let message = new MessageDefault(Flags.Reply, from.sid(), from.event(), content);
        this._channel.send(new Frame(Flags.Reply, message), null);
    }

    /**
     * 答复并结束（即最后一次答复）
     *
     * @param from    来源消息
     * @param content 内容
     */
    replyEnd(from: Message, content: Entity) {
        let message = new MessageDefault(Flags.ReplyEnd, from.sid(), from.event(), content);
        this._channel.send(new Frame(Flags.ReplyEnd, message), null);
    }

    /**
     * 关闭
     */
    close() {
        console.debug("{} session will be closed, sessionId={}",
            this._channel.getConfig().getRoleName(), this.sessionId());

        if (this._channel.isValid()) {
            try {
                this._channel.sendClose();
            } catch (e) {
                console.warn("{} channel sendClose error",
                    this._channel.getConfig().getRoleName(), e);
            }
        }

        this._channel.close(Constants.CLOSE4_USER);
    }
}