//
//  CodecDefault.swift
//  socketd
//
//  Created by noear on 2024/1/13.
//

import Foundation

class CodecDefault : Codec {
    init(_ config:Config){
        
    }
    
    func read(_ buffer: CodecReader) -> Frame {
        
    }
    
    func write<T>(_ frame: Frame, _ targetFactory: ((Int32) -> T)) -> T where T : CodecWriter {
        
    }
}
