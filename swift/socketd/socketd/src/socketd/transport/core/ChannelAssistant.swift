//
//  ChannelAssistant.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

protocol ChannelAssistant{
    associatedtype T;
    
    /**
     * 写入
     *
     * @param target 目标
     * @param frame  帧
     */
    func write(_ target:T, _ frame:Frame);
    
    /**
     * 是否有效
     */
    func isValid(_ target:T) -> Bool;
    
    /**
     * 关闭
     */
    func close(_ target:T);
    
    /**
     * 获取远程地址
     */
    //func getRemoteAddress(_ target:T) -> InetSocketAddress;
    
    /**
     * 获取本地地址
     */
    //func getLocalAddress(_ target:T) -> InetSocketAddress;
}
