package org.noear.socketd.protocol;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 消息实例
 *
 * @author noear
 * @since 2.0
 */
public class Entity {

    protected String header = Constants.DEF_HEADER;
    protected byte[] body = Constants.DEF_BODY;

    public Entity() {

    }

    public Entity(byte[] body) {
        this.body = body;
    }

    public Entity(String body){
        this.body = body.getBytes(StandardCharsets.UTF_8);
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
        return "Entity{" +
                "header='" + header + '\'' +
                ", body=" + Arrays.toString(body) +
                '}';
    }
}
