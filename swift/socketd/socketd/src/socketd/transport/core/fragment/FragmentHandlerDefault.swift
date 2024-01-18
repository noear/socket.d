//
//  FragmentHandlerDefault.swift
//  socketd
//
//  Created by noear on 2024/1/13.
//

import Foundation

class FragmentHandlerDefault : FragmentHandler{
    func nextFragment(_ channel: Channel, _ stream: StreamInternal, _ message: MessageInternal, _ consumer: (Entity) -> ()) {
        
    }
    
    func aggrFragment(_ channel: Channel, _ fragmentIndex: Int32, _ message: MessageInternal) -> Frame? {
        return nil;
    }
    
    func aggrEnable() -> Bool {
        return true;
    }
}
