namespace org.noear.socketd.transport.core;

public static class Flags
{
    /**
     * 未知
     */
    public const int Unknown = 0;

    /**
     * 连接
     */
    public const int Connect = 10; //握手：连接(c->s)，提交客户端握手信息，请求服务端握手信息

    /**
     * 连接确认
     */
    public const int Connack = 11; //握手：确认(c<-s)，响应服务端握手信息

    /**
     * Ping
     */
    public const int Ping = 20; //心跳:ping(c<->s)

    /**
     * Pong
     */
    public const int Pong = 21; //心跳:pong(c<->s)

    /**
     * 关闭（Udp 没有断链的概念，需要发消息）
     */
    public const int Close = 30;

    /**
     * 告警
     */
    public const int Alarm = 31;

    /**
     * 消息
     */
    public const int Message = 40; //消息(c<->s)

    /**
     * 请求
     */
    public const int Request = 41; //请求(c<->s)

    /**
     * 订阅
     */
    public const int Subscribe = 42;

    /**
     * 答复
     */
    public const int Reply = 48;

    /**
     * 答复结束（结束订阅接收）
     */
    public const int ReplyEnd = 49;

    public static int of(int code)
    {
        switch (code)
        {
            case 10:
                return Connect;
            case 11:
                return Connack;
            case 20:
                return Ping;
            case 21:
                return Pong;
            case 30:
                return Close;
            case 31:
                return Alarm;
            case 40:
                return Message;
            case 41:
                return Request;
            case 42:
                return Subscribe;
            case 48:
                return Reply;
            case 49:
                return ReplyEnd;
            default:
                return Unknown;
        }
    }

    public static String name(int code)
    {
        switch (code)
        {
            case Connect:
                return "Connect";
            case Connack:
                return "Connack";
            case Ping:
                return "Ping";
            case Pong:
                return "Pong";
            case Close:
                return "Close";
            case Alarm:
                return "Alarm";
            case Message:
                return "Message";
            case Request:
                return "Request";
            case Subscribe:
                return "Subscribe";
            case Reply:
                return "Reply";
            case ReplyEnd:
                return "ReplyEnd";
            default:
                return "Unknown";
        }
    }
}