//
//  SubscribeStreamImpl.swift
//  socketd
//
//  Created by noear on 2024/1/12.
//

import Foundation


class SubscribeStreamImpl : StreamBase, SubscribeStream{
    /**
     * 答复发生时
     */
    func  thenReply(_ onReply:IoConsumer<Reply>) -> Self{
        return self;
    }
}
