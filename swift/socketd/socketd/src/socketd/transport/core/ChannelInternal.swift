//
//  ChannelInternal.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 通道内部扩展
 *
 * @author noear
 * @since 2.0
 */
protocol ChannelInternal : Channel{
    /**
     * 设置会话
     */
    func setSession(_ session:Session);
    
    /**
     * 当打开时
     */
    func onOpenFuture(_ future: (_ isOk:Bool,_ error:Error)->());
    
    /**
     * 执行打开时
     */
    func doOpenFuture(_ isOk:Bool, _ error:Error);

}
