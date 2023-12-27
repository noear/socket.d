//
//  IdGenerator.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * Id 生成器
 *
 * @author noear
 * @since 2.0
 */
protocol IdGenerator{
    /**
     * 生成
     */
    func generate() -> String;
}
