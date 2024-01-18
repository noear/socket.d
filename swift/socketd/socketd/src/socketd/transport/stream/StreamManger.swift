//
//  StreamManger.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 流管理器
 *
 * @author noear
 * @since 2.0
 */
protocol StreamManger{
    /**
     * 添加流
     *
     * @param sid    流Id
     * @param stream 流
     */
    func addStream(_ sid:String, _ stream: any StreamInternal);
    
    /**
     * 获取流
     *
     * @param sid 流Id
     */
    func getStream(_ sid:String) -> any StreamInternal?;
    
    /**
     * 移除流
     *
     * @param sid 流Id
     */
    func removeStream(_ sid:String);
}
