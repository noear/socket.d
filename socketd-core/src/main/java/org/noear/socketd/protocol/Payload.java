package org.noear.socketd.protocol;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 2.0
 */
public class Payload {
    private String key;
    private String resourceDescriptor;
    private String headers;
    private byte[] body;

    public Payload(String headers) {
        this("", "", headers, new byte[]{});
    }

    public Payload(String resourceDescriptor, String headers) {
        this("", resourceDescriptor, headers, new byte[]{});
    }

    public Payload(String key, String resourceDescriptor, String headers, byte[] body) {
        this.key = key;
        this.resourceDescriptor = resourceDescriptor;
        this.headers = headers;
        this.body = body;
    }

    /**
     * Key
     */
    public String getKey() {
        return key;
    }

    /**
     * resourceDescriptor
     */
    public String getResourceDescriptor() {
        return resourceDescriptor;
    }

    /**
     * Header
     */
    public String getHeaders() {
        return headers;
    }

    Map<String,String> headerMap;
    public Map<String,String> getHeaderMap() {
        if (headers == null) {
            return null;
        }

        if (headerMap == null) {
            headerMap = new LinkedHashMap<>();

            for (String kvStr : headers.split("&")) {
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
}
