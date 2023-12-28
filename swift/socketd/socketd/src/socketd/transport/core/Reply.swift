//
//  Reply.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 答复实体
 *
 * @author noear
 * @since 2.1
 */
protocol Reply : Entity{
    /**
     * 流Id
     */
    func sid() -> String;
    
    /**
     * 是否答复结束
     */
    func isEnd() -> Bool;
}
