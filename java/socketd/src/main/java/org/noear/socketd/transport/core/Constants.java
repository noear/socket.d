package org.noear.socketd.transport.core;

import java.nio.ByteBuffer;

/**
 * 常量
 *
 * @author noear
 * @since 2.0
 */
public interface Constants {
    /**
     * 默认流id（占位）
     */
    String DEF_SID = "";
    /**
     * 默认事件（占位）
     */
    String DEF_EVENT = "";
    /**
     * 默认元信息字符串（占位）
     */
    String DEF_META_STRING = "";
    /**
     * 默认数据（占位）
     */
    ByteBuffer DEF_DATA = ByteBuffer.wrap(new byte[]{});
    /**
     * 默认端口
     * */
    int DEF_PORT = 8602;

    /**
     * 附件键-通道
     * */
    String ATT_KEY_CHANNEL = "ATT_KEY_SD_CHANNEL";

    /**
     * 因协议指令关闭
     */
    int CLOSE11_PROTOCOL = 11;
    /**
     * 因协议非法关闭
     */
    int CLOSE12_PROTOCOL_ILLEGAL = 12;
    /**
     * 因协议指令用户主动关闭（不可再重连）
     */
    int CLOSE19_PROTOCOL_USER = 19;
    /**
     * 因异常关闭
     */
    int CLOSE21_ERROR = 21;
    /**
     * 因重连关闭
     */
    int CLOSE22_RECONNECT = 22;
    /**
     * 因打开失败关闭
     */
    int CLOSE28_OPEN_FAIL = 28;
    /**
     * 因用户主动关闭（不可再重连）
     */
    int CLOSE29_USER = 29;


    /**
     * 流ID长度最大限制
     */
    int MAX_SIZE_SID = 64;
    /**
     * 事件长度最大限制
     */
    int MAX_SIZE_EVENT = 512;
    /**
     * 元信息串长度最大限制
     */
    int MAX_SIZE_META_STRING = 4096;
    /**
     * 数据长度最大限制（也是分片长度最大限制）
     */
    int MAX_SIZE_DATA = 1024 * 1024 * 16; //16m
    /**
     * 帧长度最大限制
     */
    int MAX_SIZE_FRAME = 1024 * 1024 * 17; //17m

    /**
     * 分片长度最小限制
     */
    int MIN_FRAGMENT_SIZE = 1024; //1k

    /**
     * 零需求
     */
    int DEMANDS_ZERO = 0;

    /**
     * 单需求
     */
    int DEMANDS_SINGLE = 1;

    /**
     * 多需求
     */
    int DEMANDS_MULTIPLE = 2;
}
