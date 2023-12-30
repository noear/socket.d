//
//  BufferWriter.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 编解码缓冲写
 *
 * @author noear
 * @since 2.0
 */
protocol CodecWriter{
    /**
     * 推入一组 byte
     */
    func putBytes( bytes:[Int8]) ;
    
    /**
     * 推入 int
     */
    func putInt( val:Int32) ;
    
    /**
     * 推入 char
     */
    func putChar( val:Int16) ;
    
    /**
     * 冲刷
     */
    func flush() ;
}
