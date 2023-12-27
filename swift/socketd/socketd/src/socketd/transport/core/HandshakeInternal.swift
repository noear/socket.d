//
//  HandshakeInternal.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

protocol HandshakeInternal : Handshake{
    /**
        * 获取消息源
        */
    func getSource() -> MessageInternal;
}
