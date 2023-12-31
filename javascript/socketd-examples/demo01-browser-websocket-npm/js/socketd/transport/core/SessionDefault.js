"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.SessionDefault = void 0;
const Session_1 = require("./Session");
const Message_1 = require("./Message");
const Frame_1 = require("./Frame");
const Constants_1 = require("./Constants");
const Stream_1 = require("./Stream");
/**
 * 会话默认实现
 *
 * @author noear
 * @since 2.0
 */
class SessionDefault extends Session_1.SessionBase {
    constructor(channel) {
        super(channel);
    }
    isValid() {
        return this._channel.isValid();
    }
    handshake() {
        return this._channel.getHandshake();
    }
    /**
     * 获取握手参数
     *
     * @param name 名字
     */
    param(name) {
        return this.handshake().param(name);
    }
    /**
     * 获取握手参数或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    paramOrDefault(name, def) {
        return this.handshake().paramOrDefault(name, def);
    }
    /**
     * 获取路径
     */
    path() {
        if (this._pathNew == null) {
            return this.handshake().uri().pathname;
        }
        else {
            return this._pathNew;
        }
    }
    /**
     * 设置新路径
     */
    pathNew(pathNew) {
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
    sendAlarm(from, alarm) {
        this._channel.sendAlarm(from, alarm);
    }
    /**
     * 发送
     */
    send(event, content) {
        const message = new Message_1.MessageBuilder()
            .sid(this.generateId())
            .event(event)
            .entity(content)
            .build();
        this._channel.send(new Frame_1.Frame(Constants_1.Flags.Message, message), null);
    }
    /**
     * 发送并请求（限为一次答复；指定超时）
     *
     * @param event    事件
     * @param content  内容
     * @param consumer 回调消费者
     * @param timeout 超时
     */
    sendAndRequest(event, content, consumer, timeout) {
        //异步，用 streamTimeout
        const message = new Message_1.MessageBuilder()
            .sid(this.generateId())
            .event(event)
            .entity(content)
            .build();
        const stream = new Stream_1.StreamRequest(message.sid(), timeout, consumer);
        this._channel.send(new Frame_1.Frame(Constants_1.Flags.Request, message), stream);
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
    sendAndSubscribe(event, content, consumer, timeout) {
        const message = new Message_1.MessageBuilder()
            .sid(this.generateId())
            .event(event)
            .entity(content)
            .build();
        const stream = new Stream_1.StreamSubscribe(message.sid(), timeout, consumer);
        this._channel.send(new Frame_1.Frame(Constants_1.Flags.Subscribe, message), stream);
        return stream;
    }
    /**
     * 答复
     *
     * @param from    来源消息
     * @param content 内容
     */
    reply(from, content) {
        const message = new Message_1.MessageBuilder()
            .sid(from.sid())
            .event(from.event())
            .entity(content)
            .build();
        this._channel.send(new Frame_1.Frame(Constants_1.Flags.Reply, message), null);
    }
    /**
     * 答复并结束（即最后一次答复）
     *
     * @param from    来源消息
     * @param content 内容
     */
    replyEnd(from, content) {
        const message = new Message_1.MessageBuilder()
            .sid(from.sid())
            .event(from.event())
            .entity(content)
            .build();
        this._channel.send(new Frame_1.Frame(Constants_1.Flags.ReplyEnd, message), null);
    }
    /**
     * 关闭
     */
    close() {
        console.debug(`${this._channel.getConfig().getRoleName()} session will be closed, sessionId=${this.sessionId()}`);
        if (this._channel.isValid()) {
            try {
                this._channel.sendClose();
            }
            catch (e) {
                console.warn(`${this._channel.getConfig().getRoleName()} channel sendClose error`, e);
            }
        }
        this._channel.close(Constants_1.Constants.CLOSE4_USER);
    }
}
exports.SessionDefault = SessionDefault;
