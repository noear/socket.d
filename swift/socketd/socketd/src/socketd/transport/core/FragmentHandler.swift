//
//  FragmentHandler.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 数据分片处理（分片必须做，聚合可开关）
 *
 * @author noear
 * @since 2.0
 */
protocol FragmentHandler{
    /**
     * 获取下个分片
     *
     * @param channel       通道
     * @param fragmentIndex 分片索引（由导引安排，从1按序递进）
     * @param message       总包消息
     */
    func nextFragment(_ channel:Channel, _ stream:StreamInternal, _ message:MessageInternal,_ consumer:IoConsumer<Entity>);
    
    /**
     * 聚合所有分片
     *
     * @param channel       通道
     * @param fragmentIndex 分片索引（传过来信息，不一定有顺序）
     * @param message       分片消息
     */
    func aggrFragment(_ channel:Channel, _ fragmentIndex:Int32, _ message:MessageInternal) -> Frame?;
    
    /**
     * 聚合启用
     */
    func aggrEnable()->Bool;
    
}
