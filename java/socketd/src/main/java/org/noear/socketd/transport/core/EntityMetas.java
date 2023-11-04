package org.noear.socketd.transport.core;

/**
 * 实体元信息常用名
 *
 * @author noear
 * @since 2.0
 */
public interface EntityMetas {
    /**
     * 框架版本号
     */
    String META_SOCKETD_VERSION = "SocketD-Version";
    /**
     * 数据长度
     */
    String META_DATA_LENGTH = "Data-Length";
    /**
     * 数据分片索引
     */
    String META_DATA_RANGE_IDX = "Data-Range-Idx";
    /**
     * 数据描述之文件名
     */
    String META_DATA_DISPOSITION_FILENAME = "Data-Disposition-Filename";
}
