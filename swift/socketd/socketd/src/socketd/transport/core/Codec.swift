//
//  Codec.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 编解码器
 *
 * @author noear
 * @since 2.0
 */
protocol Codec{
    /**
     * 编码读取
     *
     * @param buffer 缓冲
     */
    func read(_ buffer:BufferReader) -> Frame;
    
    /**
     * 解码写入
     *
     * @param frame         帧
     * @param targetFactory 目标工厂
     */
    func write<T:BufferWriter>(_ frame:Frame, _ targetFactory:((_ n:Int32)->T))->T;
}
