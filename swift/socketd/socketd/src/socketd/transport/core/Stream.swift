//
//  Stream.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 流接口
 *
 * @author noear
 * @since 2.1
 */
protocol Stream{
    /**
     * 流Id
     */
    func sid() -> String;
    
    /**
     * 是否单收
     */
    func isSingle() -> Bool;
    
    /**
     * 是否完成
     */
    func isDone() -> Bool;
    
    /**
     * 超时设定（单位：毫秒）
     */
    func timeout() -> Int64;
    
    /**
     * 异常发生时
     */
    func thenError(_error:Error) -> Stream;
}
