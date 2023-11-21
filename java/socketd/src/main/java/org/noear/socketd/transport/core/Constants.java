package org.noear.socketd.transport.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
     * 默认路由（占位）
     */
    String DEF_ROUTE = "";
    /**
     * 默认元信息字符串（占位）
     */
    String DEF_META_STRING = "";
    /**
     * 默认数据（占位）
     */
    InputStream DEF_DATA = new ByteArrayInputStream(new byte[]{});


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
     * 路由大小限制
     */
    int MAX_SIZE_ROUTE = 512;
    /**
     * 元信息串大小限制
     */
    int MAX_SIZE_META_STRING = 4096;
    /**
     * 分片大小限制
     */
    int MAX_SIZE_FRAGMENT = 1024 * 1024 * 16; //16m
}
