package org.noear.socketd.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author noear 2023/10/14 created
 */
public class CodecByteBuffer implements Codec<ByteBuffer>{

    private Charset charset = StandardCharsets.UTF_8;

    @Override
    public ByteBuffer encode(Frame frame) {
        if (frame.getPayload() == null) {
            //length (flag + int.bytes)
            int len =  4 + 4;

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
            //resourceDescriptor
            byte[] resourceDescriptorB = frame.getPayload().getResourceDescriptor().getBytes(charset);
            //header
            byte[] headerB = frame.getPayload().getHeaders().getBytes(charset);

            //length (flag + key + resourceDescriptor + body + int.bytes + \n*3)
            int len = keyB.length + resourceDescriptorB.length + headerB.length + frame.getPayload().getBody().length + 2 * 3 + 4 + 4;

            ByteBuffer buffer = ByteBuffer.allocate(len);

            //长度
            buffer.putInt(len);

            //flag
            buffer.putInt(frame.getFlag().getCode());

            //key
            buffer.put(keyB);
            buffer.putChar('\n');

            //resourceDescriptor
            buffer.put(resourceDescriptorB);
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

            //1.解码key and resourceDescriptor
            ByteBuffer sb = ByteBuffer.allocate(Math.min(4096, buffer.limit()));

            //key
            String key = decodeString(buffer, sb, 256);
            if (key == null) {
                return null;
            }

            //resourceDescriptor
            String resourceDescriptor = decodeString(buffer, sb, 512);
            if (resourceDescriptor == null) {
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

            Payload payload = new Payload(key, resourceDescriptor, headers, body);
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
