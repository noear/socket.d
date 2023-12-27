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
    func read(_ buffer:BufferReader) -> Frame;
    
    func write<T:BufferWriter>(_ frame:Frame, _ targetFactory:((_ n:Int32)->T))->T;
}
