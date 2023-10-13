package org.noear.socketd.protocol;

/**
 * 标志
 *
 * @author noear
 * @since 2.0
 */
public enum Flag {
    Connect, //握手：连接(c->s)，提交客户端握手信息，请求服务端握手信息
    Connack,//握手：确认(c<-s)，响应服务端握手信息
    Ping, //心跳:ping(c<->s)
    Pong, //心跳:pong(c<->s)
    Message, //消息(c<->s)
    Request, //请求(c<->s)
    ;
}
