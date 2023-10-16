package org.noear.socketd.protocol;

/**
 * 标志
 *
 * @author noear
 * @since 2.0
 */
public enum Flag {
    Unknown(0),
    Connect(10), //握手：连接(c->s)，提交客户端握手信息，请求服务端握手信息
    Connack(11),//握手：确认(c<-s)，响应服务端握手信息
    Ping(20), //心跳:ping(c<->s)
    Pong(21), //心跳:pong(c<->s)
    Close(30),
    Message(40), //消息(c<->s)
    Request(41), //请求(c<->s)
    Subscribe(42),
    Reply(49),
    ;
    int code;

    public int getCode(){
        return code;
    }

    Flag(int code){
        this.code = code;
    }

    public static Flag Of(int code){
        switch (code){
            case 10: return Connect;
            case 11: return Connack;
            case 20: return Ping;
            case 21: return Pong;
            case 30: return Close;
            case 40: return Message;
            case 41: return Request;
            case 42: return Subscribe;
            case 49: return Reply;
            default: return Unknown;
        }
    }
}
