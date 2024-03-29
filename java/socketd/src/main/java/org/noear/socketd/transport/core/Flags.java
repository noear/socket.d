package org.noear.socketd.transport.core;

/**
 * 标志
 *
 * @author noear
 * @since 2.0
 */
public interface Flags {
    /**
     * 未知
     */
    int Unknown = 0;
    /**
     * 连接
     */
    int Connect = 10; //握手：连接(c->s)，提交客户端握手信息，请求服务端握手信息
    /**
     * 连接确认
     */
    int Connack = 11; //握手：确认(c<-s)，响应服务端握手信息
    /**
     * Ping
     */
    int Ping = 20; //心跳:ping(c<->s)
    /**
     * Pong
     */
    int Pong = 21; //心跳:pong(c<->s)
    /**
     * 关闭（Udp 没有断链的概念，需要发消息）
     */
    int Close = 30;
    /**
     * 告警
     */
    int Alarm = 31;
    /**
     * 压力（预留做背压控制）
     */
    int Pressure = 32;
    /**
     * 消息
     */
    int Message = 40; //消息(c<->s)
    /**
     * 请求
     */
    int Request = 41; //请求(c<->s)
    /**
     * 订阅
     */
    int Subscribe = 42;
    /**
     * 答复
     */
    int Reply = 48;
    /**
     * 答复结束（结束订阅接收）
     */
    int ReplyEnd = 49;

    static int of(int code) {
        switch (code) {
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
            case 32:
                return Pressure;
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

    static String name(int code) {
        switch (code) {
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
            case Pressure:
                return "Pressure";
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