//
//  Frame.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 帧（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
class Frame{
    private var _flag:Int32;
    private var _message:MessageInternal;
    
    init(_ flag:Int32,_  message:MessageInternal) {
        self._flag = flag;
        self._message = message;
    }
    
    /**
     * 标志（保持与 Message 的获取风格）
     * */
    func flag() -> Int32{
        return self._flag;
    }
    
    /**
     * 消息
     * */
    func message() -> MessageInternal{
        return self._message;
    }
}
