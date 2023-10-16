package org.noear.socketd.protocol;

/**
 * @author noear
 * @since 2.0
 */
public class Payload {
    private String key;
    private String resourceDescriptor;
    private String headers;
    private byte[] body;

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

    /**
     * Body
     */
    public byte[] getBody() {
        return body;
    }
}
