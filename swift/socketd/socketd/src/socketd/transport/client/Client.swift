//
//  Client.swift
//  socketd
//
//  Created by noear on 2024/1/12.
//

import Foundation


/**
 * 客户端（用于构建会话）
 *
 * @author noear
 * @since 2.0
 */
protocol Client{
    /**
     * 心跳
     */
    func heartbeatHandler(_ handler:HeartbeatHandler) ->Client;
    
    /**
     * 配置
     */
    func config(_ configHandler:ClientConfigHandler)->Client;
    
    /**
     * 监听
     */
    func listen(_ listener:Listener)->Client;
    
    /**
     * 打开会话
     */
    func  open() ->ClientSession;
}
