namespace org.noear.socketd.transport.core;

public static class EntityMetas
{
    /**
     * 框架版本号
     */
    public const string META_SOCKETD_VERSION = "SocketD";

    /**
     * 数据长度
     */
    public const string META_DATA_LENGTH = "Data-Length";

    /**
     * 数据类型
     */
    public const string META_DATA_TYPE = "Data-Type";

    /**
     * 数据分片索引
     */
    public const string META_DATA_FRAGMENT_IDX = "Data-Fragment-Idx";

    /**
     * 数据描述之文件名
     */
    public const string META_DATA_DISPOSITION_FILENAME = "Data-Disposition-Filename";
}