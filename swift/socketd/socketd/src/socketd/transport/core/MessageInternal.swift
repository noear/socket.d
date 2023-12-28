//
//  MessageInternal.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 消息内部接口
 *
 * @author noear
 * @since 2.0
 */
protocol MessageInternal : Message, Reply{
    /**
     * 获取标记
     */
    func flag() -> Int32;
}
