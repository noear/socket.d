//
//  Channel.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation


/**
 * 通道
 *
 * @author noear
 * @since 2.0
 */
protocol Channel{
    /**
     * 获取附件
     */
    func getAttachment<T>(_ name:String) -> T;
    /**
     * 放置附件
     */
    func putAttachment(_ name:String, _ val:Any);
    /**
     * 是否有效
     */
    func isValid() -> Bool;
    /**
     * 是否已关闭
     */
    func isClosed() -> Int32;
    /**
     * 关闭（1协议关，2用户关）
     */
    func close(_ code:Int32);
    
    /**
     * 获取配置
     */
    func getConfig() -> Config;
    
    /**
     * 设置握手信息
     *
     * @param handshake 握手信息
     */
    func setHandshake(_ handshake: HandshakeInternal);
    /**
     * 获取握手信息
     */
    func getHandshake() -> HandshakeInternal;
    
    /**
     * 发送连接（握手）
     *
     * @param url 连接地址
     */
    func sendConnect(_ url:String);
    /**
     * 发送连接确认（握手）
     *
     * @param connectMessage 连接消息
     */
    func sendConnack(_ connectMessage:Message);
    /**
     * 发送 Ping（心跳）
     */
    func sendPing();
    
    /**
     * 发送 Pong（心跳）
     */
    func sendPong();
    /**
     * 发送 Close
     */
    func sendClose();
    /**
     * 发送告警
     */
    func sendAlarm(_ from:Message, _ alarm:String);
    /**
     * 发送
     *
     * @param frame  帧
     * @param stream 流（没有则为 null）
     */
    func send(_ frame:Frame, _ stream:StreamInternal);
    
    /**
     * 接收（接收答复帧）
     *
     * @param frame 帧
     */
    func retrieve(_ frame:Frame);
    
    /**
     * 手动重连（一般是自动）
     */
    func reconnect();
    
    /**
     * 出错时
     */
    func onError();
    
    /**
     * 获取会话
     */
    func getSession() -> Session;
}
