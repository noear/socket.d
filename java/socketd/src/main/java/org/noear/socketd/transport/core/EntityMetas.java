package org.noear.socketd.transport.core;

/**
 * 实体元信息常用名
 *
 * @author noear
 * @since 2.0
 */
public interface EntityMetas {
    /**
     * 协议版本号
     */
    String META_SOCKETD_VERSION = "Socket.D";

    /**
     * 发起端真实IP
     * */
    String META_X_REAL_IP = "X-Real-IP";

    /**
     * 负载均衡哈希
     * */
    String META_X_Hash = "X-Hash";

    /**
     * 数据长度
     */
    String META_DATA_LENGTH = "Data-Length";
    /**
     * 数据类型
     */
    String META_DATA_TYPE = "Data-Type";
    /**
     * 数据分片索引
     */
    String META_DATA_FRAGMENT_IDX = "Data-Fragment-Idx";
    /**
     * 数据分片总数
     */
    String META_DATA_FRAGMENT_TOTAL = "Data-Fragment-Total";
    /**
     * 数据描述之文件名
     */
    String META_DATA_DISPOSITION_FILENAME = "Data-Disposition-Filename";

    /**
     * 数据范围开始（相当于分页）
     */
    String META_RANGE_START = "Data-Range-Start";
    /**
     * 数据范围长度
     */
    String META_RANGE_SIZE = "Data-Range-Size";
}
