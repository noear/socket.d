
/**
 * 标志
 *
 * @author noear
 * @since 2.0
 */
export const Flags = {
    /**
     * 未知
     */
    Unknown: 0,
    /**
     * 连接
     */
    Connect: 10, //握手：连接(c->s)，提交客户端握手信息，请求服务端握手信息
    /**
     * 连接确认
     */
    Connack: 11,//握手：确认(c<-s)，响应服务端握手信息
    /**
     * Ping
     */
    Ping: 20,//心跳:ping(c<->s)
    /**
     * Pong
     */
    Pong: 21, //心跳:pong(c<->s)
    /**
     * 关闭（Udp 没有断链的概念，需要发消息）
     */
    Close: 30,
    /**
     * 告警
     */
    Alarm: 31,
    /**
     * 压力（预留做背压控制）
     */
    Pressure: 32,
    /**
     * 消息
     */
    Message: 40, //消息(c<->s)
    /**
     * 请求
     */
    Request: 41, //请求(c<->s)
    /**
     * 订阅
     */
    Subscribe: 42,
    /**
     * 答复
     */
    Reply: 48,
    /**
     * 答复结束（结束订阅接收）
     */
    ReplyEnd: 49,

    of: function (code: number) {
        switch (code) {
            case 10:
                return this.Connect;
            case 11:
                return this.Connack;
            case 20:
                return this.Ping;
            case 21:
                return this.Pong;
            case 30:
                return this.Close;
            case 31:
                return this.Alarm;
            case 32:
                return this.Pressure;
            case 40:
                return this.Message;
            case 41:
                return this.Request;
            case 42:
                return this.Subscribe;
            case 48:
                return this.Reply;
            case 49:
                return this.ReplyEnd;
            default:
                return this.Unknown;
        }
    },
    name: function (code: number) {
        switch (code) {
            case this.Connect:
                return "Connect";
            case this.Connack:
                return "Connack";
            case this.Ping:
                return "Ping";
            case this.Pong:
                return "Pong";
            case this.Close:
                return "Close";
            case this.Alarm:
                return "Alarm";
            case this.Pressure:
                return "Pressure";
            case this.Message:
                return "Message";
            case this.Request:
                return "Request";
            case this.Subscribe:
                return "Subscribe";
            case this.Reply:
                return "Reply";
            case this.ReplyEnd:
                return "ReplyEnd";
            default:
                return "Unknown";
        }
    }
}