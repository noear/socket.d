//
//  ChannelSupporter.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 通道支持者（创建通道的参数）
 *
 * @author noear
 * @since 2.1
 */

protocol ChannelSupporter{
    /**
     * 处理器
     */
    func getProcessor() -> Processor;
    
    /**
     * 配置
     */
    func getConfig() -> Config;
    
    /**
     * 通道助理
     */
    func getAssistant() -> ChannelAssistant;
}
