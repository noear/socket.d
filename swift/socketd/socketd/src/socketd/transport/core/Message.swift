//
//  Message.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 消息
 *
 * @author noear
 * @since 2.0
 */
protocol Message : Entity{
    /**
     * 是否为请求
     */
    func isRequest() -> Bool;
    
    /**
     * 是否为订阅
     */
    func isSubscribe() ->Bool;
    
    /**
     * 获取消息流Id（用于消息交互、分片）
     */
    func sid() -> String;
    
    /**
     * 获取消息事件
     */
    func event()->String;
    
    /**
     * 获取消息实体（有时需要获取实体）
     */
    func entity()->Entity;
    
}
