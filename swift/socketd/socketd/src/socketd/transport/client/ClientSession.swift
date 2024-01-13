//
//  ClientSession.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 客户会话
 *
 * @author noear
 */
protocol ClientSession{
    /**
     * 是否有效
     */
    func isValid() ->Bool;
    
    /**
     * 获取会话Id
     */
    func sessionId()->String;
    
    /**
     * 手动重连（一般是自动）
     */
    func reconnect();
    
    /**
     * 发送
     *
     * @param event  事件
     * @param entity 实体
     * @return 流
     */
    func send(_ event:String, _ entity:Entity) ->SendStream;
    
    /**
     * 发送并请求
     *
     * @param event  事件
     * @param entity 实体
     * @return 流
     */
    func sendAndRequest(_ event:String, _ entity:Entity) ->RequestStream ;
    
    /**
     * 发送并请求
     *
     * @param event   事件
     * @param entity  实体
     * @param timeout 超时（单位：毫秒）
     * @return 流
     */
    func sendAndRequest(_ event:String, _ entity:Entity, _ timeout:Int64) ->RequestStream;
    
    
    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event  事件
     * @param entity 实体
     * @return 流
     */
    func sendAndSubscribe(_ event:String, _ entity:Entity) -> SubscribeStream ;
    
    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event   事件
     * @param entity  实体
     * @param timeout 超时（单位：毫秒）
     * @return 流
     */
    func sendAndSubscribe(_ event:String, _ entity:Entity, _ timeout:Int64) ->SubscribeStream;
    
}
