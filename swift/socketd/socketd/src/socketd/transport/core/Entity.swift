//
//  Entity.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation


/**
 * 消息实体（帧[消息[实体]]）
 *
 * @author noear
 * @since 2.0
 */
protocol Entity{
    /**
     * at
     *
     * @since 2.1
     */
    func  at() -> String;
    
    /**
     * 获取元信息字符串（queryString style）
     */
    func metaString() -> String;
    
    /**
     * 获取元信息字典
     */
    func metaMap() -> Dictionary<String,String>;
    
    /**
     * 获取元信息
     */
    func meta(_ name:String)->String;
    
    /**
     * 获取元信息或默认
     */
    func metaOrDefault(_ name:String, _ def:String) -> String;
    
    /**
     * 放置元信息
     * */
    func putMeta(_ name:String, _ val:String);
    
    /**
     * 获取数据
     */
    func data() -> [UInt8];
    
    /**
     * 获取数据并转为字符串
     */
    func dataAsString() -> String;
    
    /**
     * 获取数据并转为字节数组
     */
    func dataAsBytes() -> [UInt8];
    
    /**
     * 获取数据长度
     */
    func dataSize() -> Int32;
    
    /**
     * 释放资源
     */
    func release();

}
