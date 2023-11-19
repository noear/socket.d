
export class Flags {
    /**
     * 未知
     */
    static Unknown: number = 0
    /**
     * 链接
     */
    static Connect: number = 10 //握手：连接(c->s)，提交客户端握手信息，请求服务端握手信息
    /**
     * 链接确认
     */
    static Connack: number = 11 //握手：确认(c<-s)，响应服务端握手信息
    /**
     * Ping
     */
    static Ping: number = 20 //心跳:ping(c<->s)
    /**
     * Pong
     */
    static Pong: number = 21 //心跳:pong(c<->s)
    /**
     * 关闭（Udp 没有断链的概念，需要发消息）
     */
    static Close: number = 30
    /**
     * 消息
     */
    static Message: number = 40 //消息(c<->s)
    /**
     * 请求
     */
    static Request: number = 41 //请求(c<->s)
    /**
     * 订阅
     */
    static Subscribe: number = 42
    /**
     * 回复
     */
    static Reply: number = 48
    /**
     * 回复结束（结束订阅接收）
     */
    static ReplyEnd: number = 49

    public static Of(code: number): number {
        switch (code) {
            case 10:
                return Flags.Connect;
            case 11:
                return Flags.Connack;
            case 20:
                return Flags.Ping;
            case 21:
                return Flags.Pong;
            case 30:
                return Flags.Close;
            case 40:
                return Flags.Message;
            case 41:
                return Flags.Request;
            case 42:
                return Flags.Subscribe;
            case 48:
                return Flags.Reply;
            case 49:
                return Flags.ReplyEnd;
            default:
                return Flags.Unknown;
        }
    }
}

