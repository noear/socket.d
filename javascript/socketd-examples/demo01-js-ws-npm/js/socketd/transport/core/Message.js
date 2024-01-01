"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.MessageDefault = exports.MessageBuilder = void 0;
const Constants_1 = require("./Constants");
/**
 * 消息默认实现（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
class MessageBuilder {
    constructor() {
        this._flag = Constants_1.Flags.Unknown;
        this._sid = Constants_1.Constants.DEF_SID;
        this._event = Constants_1.Constants.DEF_EVENT;
        this._entity = null;
    }
    /**
     * 设置标记
     */
    flag(flag) {
        this._flag = flag;
        return this;
    }
    /**
     * 设置流id
     */
    sid(sid) {
        this._sid = sid;
        return this;
    }
    /**
     * 设置事件
     */
    event(event) {
        this._event = event;
        return this;
    }
    /**
     * 设置实体
     */
    entity(entity) {
        this._entity = entity;
        return this;
    }
    /**
     * 构建
     */
    build() {
        return new MessageDefault(this._flag, this._sid, this._event, this._entity);
    }
}
exports.MessageBuilder = MessageBuilder;
/**
 * 消息默认实现（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
class MessageDefault {
    constructor(flag, sid, event, entity) {
        this._flag = flag;
        this._sid = sid;
        this._event = event;
        this._entity = entity;
    }
    at() {
        return this._entity.at();
    }
    /**
     * 获取标记
     */
    flag() {
        return this._flag;
    }
    /**
     * 是否为请求
     */
    isRequest() {
        return this._flag == Constants_1.Flags.Request;
    }
    /**
     * 是否为订阅
     */
    isSubscribe() {
        return this._flag == Constants_1.Flags.Subscribe;
    }
    /**
     * 是否答复结束
     * */
    isEnd() {
        return this._flag == Constants_1.Flags.ReplyEnd;
    }
    /**
     * 获取消息流Id（用于消息交互、分片）
     */
    sid() {
        return this._sid;
    }
    /**
     * 获取消息事件
     */
    event() {
        return this._event;
    }
    /**
     * 获取消息实体
     */
    entity() {
        return this._entity;
    }
    toString() {
        return "Message{" +
            "sid='" + this._sid + '\'' +
            ", event='" + this._event + '\'' +
            ", entity=" + this._entity +
            '}';
    }
    metaString() {
        return this._entity.metaString();
    }
    metaMap() {
        return this._entity.metaMap();
    }
    meta(name) {
        return this._entity.meta(name);
    }
    metaOrDefault(name, def) {
        return this._entity.metaOrDefault(name, def);
    }
    metaAsInt(name) {
        return this._entity.metaAsInt(name);
    }
    metaAsFloat(name) {
        return this._entity.metaAsFloat(name);
    }
    putMeta(name, val) {
        this._entity.putMeta(name, val);
    }
    data() {
        return this._entity.data();
    }
    dataAsReader() {
        return this._entity.dataAsReader();
    }
    dataAsString() {
        return this._entity.dataAsString();
    }
    dataSize() {
        return this._entity.dataSize();
    }
    release() {
        if (this._entity) {
            this._entity.release();
        }
    }
}
exports.MessageDefault = MessageDefault;
