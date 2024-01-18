//
//  ClientConnector.swift
//  socketd
//
//  Created by noear on 2024/1/12.
//

import Foundation

/**
 * 客户端连接器
 *
 * @author noear
 * @since 2.0
 */
protocol ClientConnector{
    /**
     * 获取心跳处理
     */
    func getHeartbeatHandler()->HeartbeatHandler;
    
    /**
     * 获取心跳频率（单位：毫秒）
     */
    func getHeartbeatInterval() -> Int64;
    
    /**
     * 是否自动重连
     */
    func autoReconnect() -> Bool;
    
    /**
     * 连接
     *
     * @return 通道
     */
    func connect() -> ChannelInternal;
    
    /**
     * 关闭
     */
    func close();

}
