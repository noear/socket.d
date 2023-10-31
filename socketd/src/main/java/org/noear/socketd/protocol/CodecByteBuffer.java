package org.noear.socketd.protocol;


import org.noear.socketd.protocol.entity.EntityDefault;
import org.noear.socketd.protocol.impl.MessageDefault;

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
        if (frame.getMessage() == null) {
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
            byte[] keyB = frame.getMessage().getKey().getBytes(charset);
            //topic
            byte[] topicB = frame.getMessage().getTopic().getBytes(charset);
            //metaString
            byte[] metaStringB = frame.getMessage().getEntity().getMetaString().getBytes(charset);

            //length (flag + key + topic + metaString + data + int.bytes + \n*3)
            int len = keyB.length + topicB.length + metaStringB.length + frame.getMessage().getEntity().getData().length + 2 * 3 + 4 + 4;

            ByteBuffer buffer = ByteBuffer.allocate(len);

            //长度
            buffer.putInt(len);

            //flag
            buffer.putInt(frame.getFlag().getCode());

            //key
            buffer.put(keyB);
            buffer.putChar('\n');

            //topic
            buffer.put(topicB);
            buffer.putChar('\n');

            //metaString
            buffer.put(metaStringB);
            buffer.putChar('\n');

            //data
            buffer.put(frame.getMessage().getEntity().getData());

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

            //1.解码key and topic
            ByteBuffer sb = ByteBuffer.allocate(Math.min(4096, buffer.limit()));

            //key
            String key = decodeString(buffer, sb, 256);
            if (key == null) {
                return null;
            }

            //topic
            String topic = decodeString(buffer, sb, 512);
            if (topic == null) {
                return null;
            }

            //metaString
            String metaString = decodeString(buffer, sb, 4096);
            if (metaString == null) {
                return null;
            }

            //2.解码 body
            int len = len0 - buffer.position();
            byte[] data = new byte[len];
            if (len > 0) {
                buffer.get(data, 0, len);
            }

            MessageDefault message = new MessageDefault().key(key).topic(topic).entity(new EntityDefault(metaString, data));
            message.flag(Flag.Of(flag));
            return new Frame(message.getFlag(), message);
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