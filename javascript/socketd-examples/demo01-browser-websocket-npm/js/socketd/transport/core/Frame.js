"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.Frames = exports.Frame = void 0;
const Entity_1 = require("./Entity");
const Constants_1 = require("./Constants");
const Message_1 = require("./Message");
const SocketD_1 = require("../../SocketD");
/**
 * 帧（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
class Frame {
    constructor(flag, message) {
        this._flag = flag;
        this._message = message;
    }
    /**
     * 标志（保持与 Message 的获取风格）
     * */
    flag() {
        return this._flag;
    }
    /**
     * 消息
     * */
    message() {
        return this._message;
    }
    toString() {
        return "Frame{" +
            "flag=" + Constants_1.Flags.name(this._flag) +
            ", message=" + this._message +
            '}';
    }
}
exports.Frame = Frame;
/**
 * 帧工厂
 *
 * @author noear
 * @since 2.0
 * */
class Frames {
    /**
     * 构建连接帧
     *
     * @param sid 流Id
     * @param url 连接地址
     */
    static connectFrame(sid, url) {
        const entity = new Entity_1.EntityDefault();
        //添加框架版本号
        entity.metaPut(Constants_1.EntityMetas.META_SOCKETD_VERSION, (0, SocketD_1.protocolVersion)());
        return new Frame(Constants_1.Flags.Connect, new Message_1.MessageBuilder().sid(sid).event(url).entity(entity).build());
    }
    /**
     * 构建连接确认帧
     *
     * @param connectMessage 连接消息
     */
    static connackFrame(connectMessage) {
        const entity = new Entity_1.EntityDefault();
        //添加框架版本号
        entity.metaPut(Constants_1.EntityMetas.META_SOCKETD_VERSION, (0, SocketD_1.protocolVersion)());
        return new Frame(Constants_1.Flags.Connack, new Message_1.MessageBuilder().sid(connectMessage.sid()).event(connectMessage.event()).entity(entity).build());
    }
    /**
     * 构建 ping 帧
     */
    static pingFrame() {
        return new Frame(Constants_1.Flags.Ping, null);
    }
    /**
     * 构建 pong 帧
     */
    static pongFrame() {
        return new Frame(Constants_1.Flags.Pong, null);
    }
    /**
     * 构建关闭帧（一般用不到）
     */
    static closeFrame() {
        return new Frame(Constants_1.Flags.Close, null);
    }
    /**
     * 构建告警帧（一般用不到）
     */
    static alarmFrame(from, alarm) {
        const message = new Message_1.MessageBuilder();
        if (from != null) {
            //如果有来源消息，则回传元信息
            message.sid(from.sid());
            message.event(from.event());
            message.entity(new Entity_1.StringEntity(alarm).metaStringSet(from.metaString()));
        }
        else {
            message.entity(new Entity_1.StringEntity(alarm));
        }
        return new Frame(Constants_1.Flags.Alarm, message.build());
    }
}
exports.Frames = Frames;
