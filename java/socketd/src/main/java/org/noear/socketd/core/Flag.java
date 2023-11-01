package org.noear.socketd.core;

/**
 * 标志
 *
 * @author noear
 * @since 2.0
 */
public enum Flag {
    /**
     * 未知
     */
    Unknown(0),
    /**
     * 链接
     */
    Connect(10), //握手：连接(c->s)，提交客户端握手信息，请求服务端握手信息
    /**
     * 链接确认
     */
    Connack(11),//握手：确认(c<-s)，响应服务端握手信息
    /**
     * Ping
     */
    Ping(20), //心跳:ping(c<->s)
    /**
     * Pong
     */
    Pong(21), //心跳:pong(c<->s)
    /**
     * 关闭
     */
    Close(30),
    /**
     * 消息
     */
    Message(40), //消息(c<->s)
    /**
     * 请求
     */
    Request(41), //请求(c<->s)
    /**
     * 订阅
     */
    Subscribe(42),
    /**
     * 回复
     */
    Reply(48),/**
     * 回复结束
     */
    ReplyEnd(49),
    ;
    int code;

    public int getCode() {
        return code;
    }

    Flag(int code) {
        this.code = code;
    }

    public static Flag Of(int code) {
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
}
