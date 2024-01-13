//
//  StreamInternal.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 流内部接口
 *
 * @author noear
 * @since 2.1
 */
protocol StreamInternal : Stream{
    /**
     * 获取需求数量（0，1，2）
     */
    func demands() -> Int32;
    
    /**
     * 超时设定（单位：毫秒）
     */
    func timeout() -> Int64;
    
    /**
     * 保险开始（避免永久没有回调，造成内存不能释放）
     *
     * @param streamManger  流管理器
     * @param streamTimeout 流超时
     */
    func insuranceStart(_ streamManger:StreamManger, _ streamTimeout:Int64);
    
    /**
     * 保险取消息
     */
    func insuranceCancel();
    
    /**
     * 答复时
     *
     * @param reply 答复
     */
    func onReply(_ reply:MessageInternal);
    
    /**
     * 异常时
     *
     * @param error 异常
     */
    func onError(_ error:Error);
    
    /**
     * 进度时
     *
     * @param isSend 是否为发送进度
     * @param val    当时值
     * @param max    最大值
     */
    func onProgress(_ isSend:Bool, _ val:Int32, _ max:Int32);
}
