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
     * 因协议关闭
     */
    int CLOSE1_PROTOCOL = 1;
    /**
     * 因异常关闭
     */
    int CLOSE2_ERROR = 2;
    /**
     * 因用户主动关闭
     */
    int CLOSE3_USER = 3;


    /**
     * 流ID大小限制
     */
    int MAX_SIZE_SID = 64;
    /**
     * 事件大小限制
     */
    int MAX_SIZE_EVENT = 512;
    /**
     * 元信息串大小限制
     */
    int MAX_SIZE_META_STRING = 4096;
    /**
     * 分片大小限制
     */
    int MAX_SIZE_FRAGMENT = 1024 * 1024 * 16; //16m
}
