package org.noear.socketd.protocol;

import org.noear.socketd.utils.Utils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 消息实例
 *
 * @author noear
 * @since 2.0
 */
public class Entity {

    private boolean headerChanged = false;
    protected String header = Constants.DEF_HEADER;

    protected byte[] body = Constants.DEF_BODY;

    public Entity() {

    }

    public Entity(byte[] body) {
        this.body = body;
    }

    public Entity(String body) {
        this.body = body.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Header
     */
    public String getHeader() {
        if (headerChanged) {
            StringBuilder buf = new StringBuilder();
            getHeaderMap().forEach((key, val) -> {
                buf.append(key).append("=").append(val).append("&");
            });
            if (buf.length() > 0) {
                buf.setLength(buf.length() - 1);
            }
            header = buf.toString();
            headerChanged = false;
        }

        return header;
    }

    private Map<String, String> headerMap;

    /**
     * Header as map
     */
    private Map<String, String> getHeaderMap() {
        if (headerMap == null) {
            headerMap = new LinkedHashMap<>();
            headerChanged = false;

            //此处要优化
            if (Utils.isNotEmpty(header)) {
                for (String kvStr : header.split("&")) {
                    String[] kv = kvStr.split("=");
                    headerMap.put(kv[0], kv[1]);
                }
            }
        }

        return headerMap;
    }

    public Entity putHeader(String name, String val) {
        getHeaderMap().put(name, val);
        headerChanged =true;
        return this;
    }

    public String getHeader(String name){
        return getHeaderMap().get(name);
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
                ", body=" + new String(body, StandardCharsets.UTF_8) +
                '}';
    }
}
