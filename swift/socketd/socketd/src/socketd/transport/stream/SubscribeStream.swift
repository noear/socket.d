//
//  SubscribeStream.swift
//  socketd
//
//  Created by noear on 2024/1/12.
//

import Foundation

/**
 * 订阅流
 *
 * @author noear
 * @since 2.3
 */
protocol SubscribeStream : Stream{
    /**
     * 答复发生时
     */
    func  thenReply(_ onReply:IoConsumer<Reply>) -> Self;
    
}
