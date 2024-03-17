package org.noear.socketd.transport.core.impl;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.utils.StrUtils;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 握手信息默认实现
 *
 * @author noear
 * @since 2.0
 */
public class HandshakeDefault implements HandshakeInternal {
    private final MessageInternal source;
    private final URI uri;
    private final String path;
    private final String version;
    private final Map<String, String> paramMap;
    private final Map<String, String> outMetaMap;

    /**
     * 消息源
     */
    public MessageInternal getSource() {
        return source;
    }

    public HandshakeDefault(MessageInternal source) {
        String linkUrl = source.dataAsString();
        if (StrUtils.isEmpty(linkUrl)) {
            //兼容旧版本（@deprecated 2.2）
            linkUrl = source.event();
        }

        this.source = source;
        this.uri = URI.create(linkUrl);

        this.version = source.meta(EntityMetas.META_SOCKETD_VERSION);
        this.paramMap = new ConcurrentHashMap<>();
        this.outMetaMap = new ConcurrentHashMap<>();

        if (StrUtils.isEmpty(uri.getPath())) {
            //tcp://1.1.1.1 无路径连接时，path 为空
            this.path = "/";
        } else {
            this.path = uri.getPath();
        }

        //添加连接参数
        String queryString = uri.getQuery();
        if (StrUtils.isNotEmpty(queryString)) {
            for (String kvStr : queryString.split("&")) {
                int idx = kvStr.indexOf('=');
                if (idx > 0) {
                    paramMap.put(kvStr.substring(0, idx), kvStr.substring(idx + 1));
                }
            }
        }

        //添加元信息参数
        paramMap.putAll(source.metaMap());
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
     * 请求路径
     */
    @Override
    public String path() {
        return path;
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
     * 设置参数
     *
     * @param name  名字
     * @param value 值
     */
    @Override
    public Handshake paramPut(String name, String value) {
        paramMap.put(name, value);
        return this;
    }

    @Override
    public void outMeta(String name, String value) {
        outMetaMap.put(name, value);
    }

    @Override
    public Map<String, String> getOutMetaMap() {
        return outMetaMap;
    }
}
