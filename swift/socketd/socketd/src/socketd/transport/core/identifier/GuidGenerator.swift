//
//  GuidGenerator.swift
//  socketd
//
//  Created by noear on 2024/1/13.
//

import Foundation

class GuidGenerator : IdGenerator{
    func generate() -> String {
        return UUID().uuidString;
    }
}
