package org.noear.socketd.transport.core;


import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.transport.core.impl.MessageDefault;
import org.noear.socketd.utils.IoUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * 编解码器（基于 ByteBuffer 编解）
 *
 * @author noear
 * @since 2.0
 */
public class CodecByteBuffer implements Codec<BufferReader, BufferWriter> {
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
    public <T extends BufferWriter> T write(Frame frame, Function<Integer,  T> factory) throws IOException {
        if (frame.getMessage() == null) {
            //length (flag + int.bytes)
            int len = Integer.BYTES + Integer.BYTES;
            T target = factory.apply(len);

            //长度
            target.putInt(len);

            //flag
            target.putInt(frame.getFlag().getCode());
            target.flush();

            return target;
        } else {
            //sid
            byte[] sidB = frame.getMessage().getSid().getBytes(config.getCharset());
            //topic
            byte[] topicB = frame.getMessage().getTopic().getBytes(config.getCharset());
            //metaString
            byte[] metaStringB = frame.getMessage().getEntity().getMetaString().getBytes(config.getCharset());

            //length (flag + sid + topic + metaString + data + int.bytes + \n*3)
            int len = sidB.length + topicB.length + metaStringB.length + frame.getMessage().getEntity().getDataSize() + 2 * 3 + Integer.BYTES + Integer.BYTES;

            T target = factory.apply(len);

            //长度
            target.putInt(len);

            //flag
            target.putInt(frame.getFlag().getCode());

            //sid
            target.putBytes(sidB);
            target.putChar('\n');

            //topic
            target.putBytes(topicB);
            target.putChar('\n');

            //metaString
            target.putBytes(metaStringB);
            target.putChar('\n');

            //data
            IoUtils.writeTo(frame.getMessage().getEntity().getData(), target);
            target.flush();

            return target;
        }
    }

    /**
     * 解码
     */
    @Override
    public Frame read(BufferReader buffer) {
        int len0 = buffer.getInt();

        if (len0 > (buffer.remaining() + Integer.BYTES)) {
            return null;
        }

        int flag = buffer.getInt();

        if (len0 == 8) {
            //len + flag
            return new Frame(Flag.Of(flag), null);
        } else {

            //1.解码 sid and topic
            ByteBuffer sb = ByteBuffer.allocate(Math.min(MAX_SIZE_META, buffer.remaining()));

            //sid
            String sid = decodeString(buffer, sb, MAX_SIZE_KEY);
            if (sid == null) {
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

            MessageDefault message = new MessageDefault().sid(sid).topic(topic).entity(new EntityDefault().metaString(metaString).data(data));
            message.flag(Flag.Of(flag));
            return new Frame(message.getFlag(), message);
        }
    }

    protected String decodeString(BufferReader buffer, ByteBuffer sb, int maxLen) {
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