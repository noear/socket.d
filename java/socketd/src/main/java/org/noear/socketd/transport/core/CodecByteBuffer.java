package org.noear.socketd.transport.core;


import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.transport.core.impl.MessageDefault;
import org.noear.socketd.exception.SocketdCodecException;
import org.noear.socketd.utils.IoUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 编解码器（基于 ByteBuffer 编解）
 *
 * @author noear
 * @since 2.0
 */
public class CodecByteBuffer implements Codec<ByteBuffer> {
    private static final int MAX_SIZE_KEY = 256;
    private static final int MAX_SIZE_TOPIC = 512;
    private static final int MAX_SIZE_META = 4096;

    private final Config config;

    public CodecByteBuffer(Config config) {
        this.config = config;
    }

    /**
     * 编码
     */
    @Override
    public ByteBuffer encode(Frame frame) {
        if (frame.getMessage() == null) {
            //length (flag + int.bytes)
            int len = Integer.BYTES + Integer.BYTES;

            ByteBuffer buffer = ByteBuffer.allocate(len);

            //长度
            buffer.putInt(len);

            //flag
            buffer.putInt(frame.getFlag().getCode());

            buffer.flip();

            return buffer;
        } else {
            //key
            byte[] keyB = frame.getMessage().getKey().getBytes(config.getCharset());
            //topic
            byte[] topicB = frame.getMessage().getTopic().getBytes(config.getCharset());
            //metaString
            byte[] metaStringB = frame.getMessage().getEntity().getMetaString().getBytes(config.getCharset());

            //length (flag + key + topic + metaString + data + int.bytes + \n*3)
            int len = keyB.length + topicB.length + metaStringB.length + frame.getMessage().getEntity().getDataSize() + 2 * 3 + Integer.BYTES + Integer.BYTES;

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
            try {
                IoUtils.writeTo(frame.getMessage().getEntity().getData(), buffer);
            } catch (IOException e) {
                throw new SocketdCodecException(e);
            }

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

        if (len0 > (buffer.remaining() + Integer.BYTES)) {
            return null;
        }

        int flag = buffer.getInt();

        if (len0 == 8) {
            //len + flag
            return new Frame(Flag.Of(flag), null);
        } else {

            //1.解码key and topic
            ByteBuffer sb = ByteBuffer.allocate(Math.min(MAX_SIZE_META, buffer.limit()));

            //key
            String key = decodeString(buffer, sb, MAX_SIZE_KEY);
            if (key == null) {
                return null;
            }

            //topic
            String topic = decodeString(buffer, sb, MAX_SIZE_TOPIC);
            if (topic == null) {
                return null;
            }

            //metaString
            String metaString = decodeString(buffer, sb, MAX_SIZE_META);
            if (metaString == null) {
                return null;
            }

            //2.解码 body
            int len = len0 - buffer.position();
            byte[] data = new byte[len];
            if (len > 0) {
                buffer.get(data, 0, len);
            }

            MessageDefault message = new MessageDefault().key(key).topic(topic).entity(new EntityDefault().metaString(metaString).data(data));
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

        return new String(sb.array(), 0, sb.limit(), config.getCharset());
    }
}