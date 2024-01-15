//
//  RequestStreamImpl.swift
//  socketd
//
//  Created by noear on 2024/1/13.
//

import Foundation

class RequestStreamImpl : StreamBase, RequestStream{
    /**
     * 异步等待获取答复
     */
    func await() -> Reply?{
        return nil;
    }
    
    /**
     * 答复发生时
     */
    func  thenReply(_ onReply:IoConsumer<Reply>) -> Self{
        return self;
    }
}
