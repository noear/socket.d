/* Generated from Java with JSweet 3.1.0 - http://www.jsweet.org */
namespace org.noear.socketd.transport.core {
    /**
     * 标志
     * 
     * @author noear
     * @since 2.0
     * @enum
     * @property {org.noear.socketd.transport.core.Flag} Unknown
     * 未知
     * @property {org.noear.socketd.transport.core.Flag} Connect
     * 链接
     * @property {org.noear.socketd.transport.core.Flag} Connack
     * 链接确认
     * @property {org.noear.socketd.transport.core.Flag} Ping
     * Ping
     * @property {org.noear.socketd.transport.core.Flag} Pong
     * Pong
     * @property {org.noear.socketd.transport.core.Flag} Close
     * 关闭（Udp 没有断链的概念，需要发消息）
     * @property {org.noear.socketd.transport.core.Flag} Message
     * 消息
     * @property {org.noear.socketd.transport.core.Flag} Request
     * 请求
     * @property {org.noear.socketd.transport.core.Flag} Subscribe
     * 订阅
     * @property {org.noear.socketd.transport.core.Flag} Reply
     * 回复
     * @property {org.noear.socketd.transport.core.Flag} ReplyEnd
     * 回复结束（结束订阅接收）
     * @class
     */
    export enum Flag {
        
        /**
         * 未知
         */
        Unknown, 
        /**
         * 链接
         */
        Connect, 
        /**
         * 链接确认
         */
        Connack, 
        /**
         * Ping
         */
        Ping, 
        /**
         * Pong
         */
        Pong, 
        /**
         * 关闭（Udp 没有断链的概念，需要发消息）
         */
        Close, 
        /**
         * 消息
         */
        Message, 
        /**
         * 请求
         */
        Request, 
        /**
         * 订阅
         */
        Subscribe, 
        /**
         * 回复
         */
        Reply, 
        /**
         * 回复结束（结束订阅接收）
         */
        ReplyEnd
    }

    /** @ignore */
    export class Flag_$WRAPPER {
        code;

        public getCode(): number {
            return this.code;
        }

        constructor(protected _$ordinal: number, protected _$name: string, code) {
            if (this.code === undefined) { this.code = 0; }
            this.code = code;
        }

        public static Of(code): Flag {
            switch((code)) {
            case 10:
                return Flag.Connect;
            case 11:
                return Flag.Connack;
            case 20:
                return Flag.Ping;
            case 21:
                return Flag.Pong;
            case 30:
                return Flag.Close;
            case 40:
                return Flag.Message;
            case 41:
                return Flag.Request;
            case 42:
                return Flag.Subscribe;
            case 48:
                return Flag.Reply;
            case 49:
                return Flag.ReplyEnd;
            default:
                return Flag.Unknown;
            }
        }
        public name(): string { return this._$name; }
        public ordinal(): number { return this._$ordinal; }
        public compareTo(other: any): number { return this._$ordinal - (isNaN(other)?other._$ordinal:other); }
    }
    Flag["__class"] = "org.noear.socketd.transport.core.Flag";
    Flag["_$wrappers"] = {0: new Flag_$WRAPPER(0, "Unknown", 0), 1: new Flag_$WRAPPER(1, "Connect", 10), 2: new Flag_$WRAPPER(2, "Connack", 11), 3: new Flag_$WRAPPER(3, "Ping", 20), 4: new Flag_$WRAPPER(4, "Pong", 21), 5: new Flag_$WRAPPER(5, "Close", 30), 6: new Flag_$WRAPPER(6, "Message", 40), 7: new Flag_$WRAPPER(7, "Request", 41), 8: new Flag_$WRAPPER(8, "Subscribe", 42), 9: new Flag_$WRAPPER(9, "Reply", 48), 10: new Flag_$WRAPPER(10, "ReplyEnd", 49)};

}

