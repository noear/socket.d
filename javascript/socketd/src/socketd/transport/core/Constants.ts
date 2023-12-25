
export const Constants = {
    CLOSE1_PROTOCOL: 1,
    CLOSE2_PROTOCOL_ILLEGAL: 2,
    CLOSE3_ERROR: 3,
    CLOSE4_USER: 4,
    MAX_SIZE_SID: 64,
    MAX_SIZE_EVENT: 512,
    MAX_SIZE_META_STRING: 4096,
    MAX_SIZE_DATA: 1024 * 1024 * 16,
    MIN_FRAGMENT_SIZE: 1024
}

export const Flags = {
    Unknown:0,
    Connect: 10,
    Connack: 11,
    Ping: 20,
    Pong: 21,
    Close: 30,
    Alarm: 31,
    Message: 40,
    Request: 41,
    Subscribe: 42,
    Reply: 48,
    ReplyEnd: 49
}

export const EntityMetas= {
    /**
     * 框架版本号
     */
    META_SOCKETD_VERSION: "SocketD",
    /**
     * 数据长度
     */
    META_DATA_LENGTH: "Data-Length",
    /**
     * 数据类型
     */
    META_DATA_TYPE: "Data-Type",
    /**
     * 数据分片索引
     */
    META_DATA_FRAGMENT_IDX: "Data-Fragment-Idx",
    /**
     * 数据描述之文件名
     */
    META_DATA_DISPOSITION_FILENAME: "Data-Disposition-Filename"
}