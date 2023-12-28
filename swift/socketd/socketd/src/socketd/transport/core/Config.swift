//
//  Config.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 配置接口
 *
 * @author noear
 * @since 2.0
 */
protocol Config{
    /**
     * 是否客户端模式
     */
    func clientMode() -> Bool;
    
    /**
     * 获取流管理器
     */
    func getStreamManger() -> StreamManger;
    
    /**
     * 获取角色名
     */
    func getRoleName() -> String;
    
    /**
     * 获取字符集
     */
    func getCharset() ->String;
    
    /**
     * 获取编解码器
     */
    func getCodec() ->Codec;
    
    /**
     * 获取Id生成器
     */
    func getIdGenerator() ->IdGenerator;
    
    /**
     * 获取分片处理器
     */
    func getFragmentHandler() ->FragmentHandler;
    
    /**
     * 获取分片大小
     */
    func getFragmentSize() -> Int32;
    
    /**
     * 获取 ssl 上下文
     */
    func getSslContext() -> SSLContext;
    
    
    /**
     * 核心线程数（第二优先）
     */
    func getCoreThreads() -> Int32;
    
    /**
     * 最大线程数
     */
    func getMaxThreads() -> Int32;
    
    /**
     * 获取读缓冲大小
     */
    func getReadBufferSize() -> Int32;
    
    /**
     * 配置读缓冲大小
     */
    func getWriteBufferSize() -> Int32;
    
    /**
     * 获取连接空闲超时（单位：毫秒）
     */
    func getIdleTimeout() -> Int64;
    
    /**
     * 获取请求超时（单位：毫秒）
     */
    func getRequestTimeout() -> Int64;
    
    /**
     * 获取消息流超时（单位：毫秒）
     */
    func getStreamTimeout() -> Int64;
    
    /**
     * 允许最大UDP包大小
     */
    func getMaxUdpSize() -> Int32;
    
}
