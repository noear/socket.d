//
//  BufferReader.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 编解码缓冲读
 *
 * @author noear
 * @since 2.0
 */
protocol CodecReader{
    /**
     * 获取 byte
     */
    func getByte() -> Int8;
    
    /**
     * 获取一组 byte
     */
    func getBytes( dst:[Int8],  offset:Int32,  length:Int32);
    
    /**
     * 获取 int
     */
    func getInt() -> Int32;
    
    /**
     * 剩余长度
     */
    func remaining() -> Int32;
    
    /**
     * 当前位置
     */
    func position() -> Int32;
}
