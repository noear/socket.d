//
//  Session.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

protocol Session : ClientSession{
    func path() -> String;
}
