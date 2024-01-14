//
//  ClientInternal.swift
//  socketd
//
//  Created by noear on 2024/1/14.
//

import Foundation

/**
 * 客户端内部接口
 *
 * @author noear
 * @since  2.1
 */
protocol ClientInternal : Client{
    /**
     * 获取心跳处理
     */
    func getHeartbeatHandler() -> HeartbeatHandler;
    
    /**
     * 获取心跳间隔（毫秒）
     */
    func getHeartbeatInterval() -> Int64;
    
    /**
     * 获取配置
     */
    func getConfig() -> ClientConfig;
    
    /**
     * 获取处理器
     */
    func getProcessor() -> Processor;
}
