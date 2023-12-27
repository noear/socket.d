//
//  Flags.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 标志
 *
 * @author noear
 * @since 2.0
 */
class Flags{
    /**
     * 未知
     */
    static let Unknown:Int32 = 0;
    /**
     * 连接
     */
    static let Connect:Int32 = 10; //握手：连接(c->s)，提交客户端握手信息，请求服务端握手信息
    /**
     * 连接确认
     */
    static let Connack:Int32 = 11; //握手：确认(c<-s)，响应服务端握手信息
    /**
     * Ping
     */
    static let Ping:Int32 = 20; //心跳:ping(c<->s)
    /**
     * Pong
     */
    static let Pong:Int32 = 21; //心跳:pong(c<->s)
    /**
     * 关闭（Udp 没有断链的概念，需要发消息）
     */
    static let Close:Int32 = 30;
    /**
     * 告警
     */
    static let Alarm:Int32 = 31;
    /**
     * 消息
     */
    static let Message:Int32 = 40; //消息(c<->s)
    /**
     * 请求
     */
    static let Request:Int32 = 41; //请求(c<->s)
    /**
     * 订阅
     */
    static let Subscribe:Int32 = 42;
    /**
     * 答复
     */
    static let Reply:Int32 = 48;
    /**
     * 答复结束（结束订阅接收）
     */
    static let ReplyEnd:Int32 = 49;
    
    static func of( code:Int32) -> Int32 {
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
    
    static func name( code:Int32) -> String{
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
