//
//  RequestStream.swift
//  socketd
//
//  Created by noear on 2024/1/12.
//

import Foundation


/**
 * 请求流
 *
 * @author noear
 * @since 2.3
 */
protocol RequestStream : Stream{
    
    /**
     * 异步等待获取答复
     */
    func await() -> Reply?;
    
    /**
     * 答复发生时
     */
    func  thenReply(_ onReply:IoConsumer<Reply>) -> Self;
}
