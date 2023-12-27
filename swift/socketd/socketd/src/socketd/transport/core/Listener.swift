//
//  Listener.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 监听器
 *
 * @author noear
 * @since 2.0
 */
protocol Listener{
    /**
     * 打开时
     *
     * @param session 会话
     */
    func onOpen(_ session:Session);
    
    /**
     * 收到消息时
     *
     * @param session 会话
     * @param message 消息
     */
    func onMessage(_ session:Session, _ message:Message) ;
    
    /**
     * 关闭时
     *
     * @param session 会话
     */
    func onClose(_ session:Session);
    
    /**
     * 出错时
     *
     * @param session 会话
     * @param error   错误信息
     */
    func onError(_ session:Session, _ error:Error);

}
