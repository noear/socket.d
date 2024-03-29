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
     * 因协议关闭开始（安全关闭）
     */
    CLOSE1000_PROTOCOL_CLOSE_STARTING: 1000,
    /**
     * 因协议指令关闭
     */
    CLOSE1001_PROTOCOL_CLOSE: 1001,
    /**
     * 因协议非法关闭
     */
    CLOSE1002_PROTOCOL_ILLEGAL: 1002,
    /**
     * 因异常关闭
     */
    CLOSE2001_ERROR: 2001,
    /**
     * 因重连关闭
     */
    CLOSE2002_RECONNECT: 2002,
    /**
     * 因连接断开
     */
    CLOSE2003_DISCONNECTION: 2003,
    /**
     * 因打开失败关闭
     */
    CLOSE2008_OPEN_FAIL :2008,
    /**
     * 因用户主动关闭（不可再重连）
     */
    CLOSE2009_USER: 2009,

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
     * 帧长度最大限制
     */
    MAX_SIZE_FRAME : 1024 * 1024 * 17, //17m

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
