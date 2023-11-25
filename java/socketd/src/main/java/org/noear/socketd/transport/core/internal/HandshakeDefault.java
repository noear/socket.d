package org.noear.socketd.transport.core.internal;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.utils.Utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 握手信息内部实现类
 *
 * @author noear
 * @since 2.0
 */
public class HandshakeDefault implements HandshakeInternal {
    private final MessageInternal source;
    private final URI uri;
    private final String version;
    private final Map<String, String> paramMap;

    /**
     * 消息源
     */
    public MessageInternal getSource() {
        return source;
    }

    public HandshakeDefault(MessageInternal source) {
        this.source = source;
        this.uri = URI.create(source.event());
        this.version = source.meta(EntityMetas.META_SOCKETD_VERSION);
        this.paramMap = new HashMap<>();

        String queryString = uri.getQuery();
        if (Utils.isNotEmpty(queryString)) {
            for (String kvStr : queryString.split("&")) {
                int idx = kvStr.indexOf('=');
                if (idx > 0) {
                    paramMap.put(kvStr.substring(0, idx), kvStr.substring(idx + 1));
                }
            }
        }
    }

    /**
     * 版本
     */
    @Override
    public String version() {
        return version;
    }

    /**
     * 获请地址
     *
     * @return tcp://192.168.0.1/path?user=1&path=2
     */
    @Override
    public URI uri() {
        return uri;
    }

    /**
     * 获取参数集合
     */
    @Override
    public Map<String, String> paramMap() {
        return paramMap;
    }

    /**
     * 获取参数
     *
     * @param name 名字
     */
    @Override
    public String param(String name) {
        return paramMap.get(name);
    }

    /**
     * 获取参数或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    @Override
    public String paramOrDefault(String name, String def) {
        return paramMap.getOrDefault(name, def);
    }

    /**
     * 设置或修改参数
     *
     * @param name  名字
     * @param value 值
     */
    @Override
    public void param(String name, String value) {
        paramMap.put(name, value);
    }
}
