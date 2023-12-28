//
//  Handshake.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 握手信息
 *
 * @author noear
 * @since 2.0
 */
protocol Handshake{
    /**
     * 协议版本
     */
    func version() -> String;
    
    /**
     * 获请传输地址
     *
     * @return tcp://192.168.0.1/path?user=1&path=2
     */
    func uri() -> String;
    
    /**
     * 获取参数集合
     */
    func paramMap() -> Dictionary<String, String>;
    
    /**
     * 获取参数
     *
     * @param name 参数名
     */
    func param(_ name:String) -> String;
    
    /**
     * 获取参数或默认值
     *
     * @param name 参数名
     * @param def  默认值
     */
    func paramOrDefault(_ name:String, _ def:String) -> String;
    
    /**
     * 设置或修改参数
     */
    func param(_ name:String, _ value:String);
}
