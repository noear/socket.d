//
//  EntityMetas.swift
//  socketd
//
//  Created by noear on 2023/12/27.
//

import Foundation

/**
 * 实体元信息常用名
 *
 * @author noear
 * @since 2.0
 */
class EntityMetas{
    /**
     * 框架版本号
     */
    static let META_SOCKETD_VERSION:String = "SocketD";
    /**
     * 数据长度
     */
    static let META_DATA_LENGTH:String = "Data-Length";
    /**
     * 数据类型
     */
    static let META_DATA_TYPE:String = "Data-Type";
    /**
     * 数据分片索引
     */
    static let META_DATA_FRAGMENT_IDX:String = "Data-Fragment-Idx";
    /**
     * 数据描述之文件名
     */
    static let META_DATA_DISPOSITION_FILENAME:String = "Data-Disposition-Filename";
}
