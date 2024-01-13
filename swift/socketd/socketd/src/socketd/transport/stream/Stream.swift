//
//  Stream.swift
//  socketd
//
//  Created by noear on 2024/1/12.
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
     * 是否完成
     */
    func isDone() -> Bool;
    
    /**
     * 异常发生时
     */
    func thenError(_ onError: @escaping IoConsumer<Error>) -> Self;
    
    /**
     * 进度发生时
     *
     * @param onProgress (isSend, val, max)
     */
    func thenProgress(_ onProgress: @escaping IoTriConsumer<Bool, Int32, Int32>) ->Self;
}
