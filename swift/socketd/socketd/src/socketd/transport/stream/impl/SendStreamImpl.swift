//
//  SendStreamImpl.swift
//  socketd
//
//  Created by noear on 2024/1/12.
//

import Foundation

class SendStreamImpl : StreamBase, SendStream{
    init(_ sid: String) {
        super.init(sid, 0, 0);
    }
    
    override func isDone() -> Bool {
        return false;
    }
}
