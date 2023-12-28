//
//  SimpleListener.swift
//  socketd
//
//  Created by noear on 2023/12/28.
//

import Foundation

/**
 * 简单监听器（一般用于占位）
 *
 * @author noear
 * @since 2.0
 */
class SimpleListener : Listener{
    func onOpen(_ session: Session) {
        
    }
    
    func onMessage(_ session: Session, _ message: Message) {
        
    }
    
    func onClose(_ session: Session) {
        
    }
    
    func onError(_ session: Session, _ error: Error) {
        
    }
    
    
}
