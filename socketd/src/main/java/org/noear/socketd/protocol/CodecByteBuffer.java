package org.noear.socketd.protocol;

import org.noear.socketd.protocol.impl.PayloadDefault;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 编解码器（基于 ByteBuffer 编解）
 *
 * @author noear
 * @since 2.0
 */
public class CodecByteBuffer implements Codec<ByteBuffer> {

    private Charset charset = StandardCharsets.UTF_8;

    /**
     * 编码
     */
    @Override
    public ByteBuffer encode(Frame frame) {
        if (frame.getPayload() == null) {
            //length (flag + int.bytes)
            int len = 4 + 4;

            ByteBuffer buffer = ByteBuffer.allocate(len);

            //长度
            buffer.putInt(len);

            //flag
            buffer.putInt(frame.getFlag().getCode());

            buffer.flip();

            return buffer;
        } else {
            //key
            byte[] keyB = frame.getPayload().getKey().getBytes(charset);
            //routeDescriptor
            byte[] routeDescriptorB = frame.getPayload().getRouteDescriptor().getBytes(charset);
            //header
            byte[] headerB = frame.getPayload().getHeader().getBytes(charset);

            //length (flag + key + routeDescriptor + body + int.bytes + \n*3)
            int len = keyB.length + routeDescriptorB.length + headerB.length + frame.getPayload().getBody().length + 2 * 3 + 4 + 4;

            ByteBuffer buffer = ByteBuffer.allocate(len);

            //长度
            buffer.putInt(len);

            //flag
            buffer.putInt(frame.getFlag().getCode());

            //key
            buffer.put(keyB);
            buffer.putChar('\n');

            //routeDescriptor
            buffer.put(routeDescriptorB);
            buffer.putChar('\n');
            //header
            buffer.put(headerB);
            buffer.putChar('\n');

            //body
            buffer.put(frame.getPayload().getBody());

            buffer.flip();

            return buffer;
        }
    }

    /**
     * 解码
     */
    @Override
    public Frame decode(ByteBuffer buffer) {
        int len0 = buffer.getInt();

        if (len0 > (buffer.remaining() + 4)) {
            return null;
        }

        int flag = buffer.getInt();

        if (len0 == 8) {
            //len + flag
            return new Frame(Flag.Of(flag), null);
        } else {

            //1.解码key and routeDescriptor
            ByteBuffer sb = ByteBuffer.allocate(Math.min(4096, buffer.limit()));

            //key
            String key = decodeString(buffer, sb, 256);
            if (key == null) {
                return null;
            }

            //routeDescriptor
            String routeDescriptor = decodeString(buffer, sb, 512);
            if (routeDescriptor == null) {
                return null;
            }

            //headers
            String headers = decodeString(buffer, sb, 4096);
            if (headers == null) {
                return null;
            }

            //2.解码 body
            int len = len0 - buffer.position();
            byte[] body = new byte[len];
            if (len > 0) {
                buffer.get(body, 0, len);
            }

            Payload payload = new PayloadDefault(key, routeDescriptor, headers, body);
            return new Frame(Flag.Of(flag), payload);
        }
    }

    protected String decodeString(ByteBuffer buffer, ByteBuffer sb, int maxLen) {
        sb.clear();

        while (true) {
            byte c = buffer.get();

            if (c == 10) { //10:'\n'
                break;
            } else if (c != 0) { //32:' '
                sb.put(c);
            }

            if (maxLen > 0 && maxLen < sb.position()) {
                return null;
            }
        }

        sb.flip();
        if (sb.limit() < 1) {
            return "";
        }

        return new String(sb.array(), 0, sb.limit());
    }
}
