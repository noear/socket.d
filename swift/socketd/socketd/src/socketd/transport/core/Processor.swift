//
//  Processor.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 处理器
 *
 * @author noear
 * @since 2.0
 */
protocol Processor{
    /**
     * 设置监听器
     */
    func setListener(_ listener:Listener);
    
    /**
     * 接收处理
     */
    func onReceive(_ channel:ChannelInternal, _ frame:Frame);
    
    /**
     * 打开时
     *
     * @param channel 通道
     */
    func onOpen(_ channel:ChannelInternal);
    
    /**
     * 收到消息时
     *
     * @param channel 通道
     * @param message 消息
     */
    func onMessage(_ channel:ChannelInternal, _ message:Message);
    
    /**
     * 关闭时
     *
     * @param channel 通道
     */
    func onClose(_ channel:ChannelInternal);
    
    /**
     * 出错时
     *
     * @param channel 通道
     * @param error   错误信息
     */
    func onError(_ channel:ChannelInternal, _ error:Error);
}
