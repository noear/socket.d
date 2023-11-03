package org.noear.socketd.transport.core;

/**
 * 常量
 *
 * @author noear
 * @since 2.0
 */
public class Constants {
    public static final String META_SOCKETD_VERSION = "SocketD-Version";

    public static final String META_CONNECT = "SocketD-Version=2.0";
    public static final String META_CONNACK = "SocketD-Version=2.0";

    /**
     * 数据长度
     */
    public static final String META_DATA_LENGTH = "Data-Length";
    /**
     * 数据分片索引
     */
    public static final String META_DATA_RANGE_IDX = "Data-Range-Idx";
    /**
     * 数据描述之文件名
     */
    public static final String META_DATA_DISPOSITION_FILENAME = "Data-Disposition-Filename";


    public static final String DEF_KEY = "";
    public static final String DEF_TOPIC = "";
    public static final String DEF_META_STRING = "";
    public static final byte[] DEF_DATA = new byte[]{};
}
