//
//  Constants.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

class Constants{
    /**
     * 默认流id（占位）
     */
    static let DEF_SID:String = "";
    /**
     * 默认事件（占位）
     */
    static let DEF_EVENT:String = "";
    /**
     * 默认元信息字符串（占位）
     */
    static let DEF_META_STRING:String = "";
    /**
     * 默认数据（占位）
     */
    static let DEF_DATA:[UInt8] =  Array("".utf8);
    
    
    /**
     * 因协议指令关闭
     */
    static let CLOSE1_PROTOCOL:Int32 = 1;
    /**
     * 因协议非法关闭
     */
    static let CLOSE2_PROTOCOL_ILLEGAL:Int32 = 2;
    /**
     * 因异常关闭
     */
    static let CLOSE3_ERROR:Int32 = 3;
    /**
     * 因用户主动关闭
     */
    static let CLOSE4_USER:Int32 = 4;
    
    
    /**
     * 流ID长度最大限制
     */
    static let MAX_SIZE_SID:Int32 = 64;
    /**
     * 事件长度最大限制
     */
    static let MAX_SIZE_EVENT:Int32 = 512;
    /**
     * 元信息串长度最大限制
     */
    static let MAX_SIZE_META_STRING:Int32 = 4096;
    /**
     * 数据长度最大限制（也是分片长度最大限制）
     */
    static let MAX_SIZE_DATA:Int32 = 1024 * 1024 * 16; //16m
    /**
     * 帧长度最大限制
     */
    static let MAX_SIZE_FRAME:Int32 = 1024 * 1024 * 17; //17m
    
    /**
     * 分片长度最小限制
     */
    static let MIN_FRAGMENT_SIZE:Int32 = 1024; //1k
}
