namespace org.noear.socketd.transport.core;

public static class Constants
{
    /**
     * 默认流id（占位）
     */
    public const string DEF_SID = "";

    /**
     * 默认事件（占位）
     */
    public const string DEF_EVENT = "";

    /**
     * 默认元信息字符串（占位）
     */
    public const string DEF_META_STRING = "";

    /**
     * 默认数据（占位）
     */
    public const byte[] DEF_DATA = null;


    /**
     * 因协议指令关闭
     */
    public const int CLOSE1_PROTOCOL = 1;

    /**
     * 因协议非法关闭
     */
    public const int CLOSE2_PROTOCOL_ILLEGAL = 2;

    /**
     * 因异常关闭
     */
    public const int CLOSE3_ERROR = 3;

    /**
     * 因用户主动关闭
     */
    public const int CLOSE4_USER = 4;


    /**
     * 流ID长度最大限制
     */
    public const int MAX_SIZE_SID = 64;

    /**
     * 事件长度最大限制
     */
    public const int MAX_SIZE_EVENT = 512;

    /**
     * 元信息串长度最大限制
     */
    public const int MAX_SIZE_META_STRING = 4096;

    /**
     * 数据长度最大限制（也是分片长度最大限制）
     */
    public const int MAX_SIZE_DATA = 1024 * 1024 * 16; //16m

    /**
     * 帧长度最大限制
     */
    public const int MAX_SIZE_FRAME = 1024 * 1024 * 17; //17m

    /**
     * 分片长度最小限制
     */
    public const int MIN_FRAGMENT_SIZE = 1024; //1k
}