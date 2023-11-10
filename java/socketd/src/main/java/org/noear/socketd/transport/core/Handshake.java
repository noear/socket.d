package org.noear.socketd.transport.core;

import java.net.URI;
import java.util.Map;

/**
 * 握手信息
 *
 * @author noear
 * @since 2.0
 */
public interface Handshake {
    /**
     * 获请地址
     *
     * @return tcp://192.168.0.1/path?user=1&path=2
     */
    URI getUri();

    /**
     * 获取传输协议
     */
    String getScheme();

    /**
     * 获取路径
     */
    String getPath();

    /**
     * 获取参数集合
     */
    Map<String, String> getParamMap();

    /**
     * 获取参数
     *
     * @param name 参数名
     */
    String getParam(String name);

    /**
     * 获取参数或默认值
     *
     * @param name 参数名
     * @param def  默认值
     */
    String getParamOrDefault(String name, String def);

    /**
     * 设置或修改参数
     */
    String putParam(String name, String value);

    /**
     * 版本
     */
    String getVersion();
}
