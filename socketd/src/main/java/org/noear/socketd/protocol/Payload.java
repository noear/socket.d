package org.noear.socketd.protocol;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 负载
 *
 * @author noear
 * @since 2.0
 */
public class Payload {
    private String key;
    private String routeDescriptor;
    private String header;
    private byte[] body;

    protected Flag flag = Flag.Unknown;

    public Payload(String header) {
        this("", "", header, new byte[]{});
    }

    public Payload(String key, String routeDescriptor, String header) {
        this(key, routeDescriptor, header, new byte[]{});
    }

    public Payload(String key, String routeDescriptor, String header, byte[] body) {
        this.key = key;
        this.routeDescriptor = routeDescriptor;
        this.header = header;
        this.body = body;
    }

    /**
     * 是否为请求
     */
    public boolean isRequest() {
        return flag == Flag.Request;
    }

    /**
     * 是否为订阅
     */
    public boolean isSubscribe() {
        return flag == Flag.Subscribe;
    }

    /**
     * Key
     */
    public String getKey() {
        return key;
    }

    /**
     * routeDescriptor
     */
    public String getRouteDescriptor() {
        return routeDescriptor;
    }

    /**
     * Header
     */
    public String getHeader() {
        return header;
    }

    Map<String, String> headerMap;

    /**
     * Header as map
     */
    public Map<String, String> getHeaderMap() {
        if (header == null) {
            return null;
        }

        if (headerMap == null) {
            headerMap = new LinkedHashMap<>();

            //此处要优化
            for (String kvStr : header.split("&")) {
                String[] kv = kvStr.split("=");
                headerMap.put(kv[0], kv[1]);
            }
        }

        return headerMap;
    }

    /**
     * Body
     */
    public byte[] getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "key='" + key + '\'' +
                ", routeDescriptor='" + routeDescriptor + '\'' +
                ", header='" + header + '\'' +
                '}';
    }
}
