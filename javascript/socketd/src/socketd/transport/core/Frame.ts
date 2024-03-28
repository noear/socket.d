import {EntityDefault, StringEntity} from "./Entity";
import {EntityMetas} from "./EntityMetas";
import {Flags} from "./Flags";
import {Message, MessageBuilder, MessageInternal} from "./Message";
import {SocketD} from "../../SocketD";
import {HandshakeInternal} from "./Handshake";

/**
 * 帧（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
export class Frame {
    private _flag: number;
    private _message: MessageInternal | null;

    constructor(flag: number, message: MessageInternal | null) {
        this._flag = flag;
        this._message = message;
    }

    /**
     * 标志（保持与 Message 的获取风格）
     * */
    flag(): number {
        return this._flag;
    }

    /**
     * 消息
     * */
    message(): MessageInternal | null {
        return this._message;
    }

    toString(): string {
        return "Frame{" +
            "flag=" + Flags.name(this._flag) +
            ", message=" + this._message +
            '}';
    }
}


/**
 * 帧工厂
 *
 * @author noear
 * @since 2.0
 * */
export class Frames {
    /**
     * 构建连接帧
     *
     * @param sid 流Id
     * @param url 连接地址
     */
    static connectFrame(sid: string, url: string, metaMap: Map<string, string>): Frame {
        const entity = new StringEntity(url);
        //添加框架版本号
        entity.metaMapPut(metaMap);
        entity.metaPut(EntityMetas.META_SOCKETD_VERSION, SocketD.protocolVersion());
        return new Frame(Flags.Connect, new MessageBuilder()
            .sid(sid)
            .event(url) //兼容旧版本（@deprecated 2.2.2）
            .entity(entity).build());
    }

    /**
     * 构建连接确认帧
     *
     * @param handshake 握手信息
     */
    static connackFrame(handshake: HandshakeInternal): Frame {
        const entity = new EntityDefault();
        //添加框架版本号
        entity.metaMapPut(handshake.getOutMetaMap());
        entity.metaPut(EntityMetas.META_SOCKETD_VERSION, SocketD.protocolVersion());
        entity.dataSet(handshake.getSource().data().getArray()!);
        return new Frame(Flags.Connack, new MessageBuilder()
            .sid(handshake.getSource().sid())
            .event(handshake.getSource().event()) //兼容旧版本（@deprecated 2.2.2）
            .entity(entity).build());
    }

    /**
     * 构建 ping 帧
     */
    static pingFrame(): Frame {
        return new Frame(Flags.Ping, null);
    }

    /**
     * 构建 pong 帧
     */
    static pongFrame(): Frame {
        return new Frame(Flags.Pong, null);
    }

    /**
     * 构建关闭帧（一般用不到）
     */
    static closeFrame(code:number): Frame {
        const message = new MessageBuilder();
        message.entity(new StringEntity("").metaPut("code", code.toString()));

        return new Frame(Flags.Close, message.build());
    }

    /**
     * 构建告警帧（一般用不到）
     */
    static alarmFrame(from: Message, alarm: string): Frame {
        const message = new MessageBuilder();

        if (from != null) {
            //如果有来源消息，则回传元信息
            message.sid(from.sid());
            message.event(from.event());
            message.entity(new StringEntity(alarm).metaStringSet(from.metaString()));
        } else {
            message.entity(new StringEntity(alarm));
        }

        return new Frame(Flags.Alarm, message.build());
    }
}
