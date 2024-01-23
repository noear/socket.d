import {ByteBuffer} from "./Buffer";

/**
 * 常量
 *
 * @author noear
 * @since 2.0
 */
export const Constants = {
    /**
     * 默认流id（占位）
     */
    DEF_SID: "",
    /**
     * 默认事件（占位）
     */
    DEF_EVENT: "",
    /**
     * 默认元信息字符串（占位）
     */
    DEF_META_STRING: "",
    /**
     * 默认数据
     * */
    DEF_DATA: new ByteBuffer(new ArrayBuffer(0)),


    /**
     * 因协议指令关闭
     */
    CLOSE11_PROTOCOL: 11,
    /**
     * 因协议非法关闭
     */
    CLOSE12_PROTOCOL_ILLEGAL: 12,
    /**
     * 因协议指令用户主动关闭（不可再重连）
     */
    CLOSE19_PROTOCOL_USER: 19,
    /**
     * 因异常关闭
     */
    CLOSE21_ERROR: 21,
    /**
     * 因重连关闭
     */
    CLOSE22_RECONNECT: 22,
    /**
     * 因打开失败关闭
     */
    CLOSE28_OPEN_FAIL :28,
    /**
     * 因用户主动关闭（不可再重连）
     */
    CLOSE29_USER: 29,

    /**
     * 流ID长度最大限制
     */
    MAX_SIZE_SID: 64,
    /**
     * 事件长度最大限制
     */
    MAX_SIZE_EVENT: 512,
    /**
     * 元信息串长度最大限制
     */
    MAX_SIZE_META_STRING: 4096,
    /**
     * 数据长度最大限制（也是分片长度最大限制）
     */
    MAX_SIZE_DATA: 1024 * 1024 * 16,

    /**
     * 分片长度最小限制
     */
    MIN_FRAGMENT_SIZE: 1024,

    /**
     * 零需求
     */
    DEMANDS_ZERO: 0,

    /**
     * 单需求
     */
    DEMANDS_SIGNLE: 1,

    /**
     * 多需要
     */
    DEMANDS_MULTIPLE: 2
}

/**
 * 标志
 *
 * @author noear
 * @since 2.0
 */
export const Flags = {
    /**
     * 未知
     */
    Unknown: 0,
    /**
     * 连接
     */
    Connect: 10, //握手：连接(c->s)，提交客户端握手信息，请求服务端握手信息
    /**
     * 连接确认
     */
    Connack: 11,//握手：确认(c<-s)，响应服务端握手信息
    /**
     * Ping
     */
    Ping: 20,//心跳:ping(c<->s)
    /**
     * Pong
     */
    Pong: 21, //心跳:pong(c<->s)
    /**
     * 关闭（Udp 没有断链的概念，需要发消息）
     */
    Close: 30,
    /**
     * 告警
     */
    Alarm: 31,
    /**
     * 消息
     */
    Message: 40, //消息(c<->s)
    /**
     * 请求
     */
    Request: 41, //请求(c<->s)
    /**
     * 订阅
     */
    Subscribe: 42,
    /**
     * 答复
     */
    Reply: 48,
    /**
     * 答复结束（结束订阅接收）
     */
    ReplyEnd: 49,

    of: function (code: number) {
        switch (code) {
            case 10:
                return this.Connect;
            case 11:
                return this.Connack;
            case 20:
                return this.Ping;
            case 21:
                return this.Pong;
            case 30:
                return this.Close;
            case 31:
                return this.Alarm;
            case 40:
                return this.Message;
            case 41:
                return this.Request;
            case 42:
                return this.Subscribe;
            case 48:
                return this.Reply;
            case 49:
                return this.ReplyEnd;
            default:
                return this.Unknown;
        }
    },
    name: function (code: number) {
        switch (code) {
            case this.Connect:
                return "Connect";
            case this.Connack:
                return "Connack";
            case this.Ping:
                return "Ping";
            case this.Pong:
                return "Pong";
            case this.Close:
                return "Close";
            case this.Alarm:
                return "Alarm";
            case this.Message:
                return "Message";
            case this.Request:
                return "Request";
            case this.Subscribe:
                return "Subscribe";
            case this.Reply:
                return "Reply";
            case this.ReplyEnd:
                return "ReplyEnd";
            default:
                return "Unknown";
        }
    }
}

/**
 * 实体元信息常用名
 *
 * @author noear
 * @since 2.0
 */
export const EntityMetas= {
    /**
     * 框架版本号
     */
    META_SOCKETD_VERSION: "Socket.D",
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
     * 数据分片总数
     */
    META_DATA_FRAGMENT_TOTAL: "Data-Fragment-Total",
    /**
     * 数据描述之文件名
     */
    META_DATA_DISPOSITION_FILENAME: "Data-Disposition-Filename",
    /**
     * 数据范围开始（相当于分页）
     */
    META_RANGE_START: "Data-Range-Start",
    /**
     * 数据范围长度
     */
    META_RANGE_SIZE: "Data-Range-Size",
}