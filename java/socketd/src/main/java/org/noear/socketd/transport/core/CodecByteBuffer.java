package org.noear.socketd.transport.core;


import org.noear.socketd.exception.SocketdSizeLimitException;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.transport.core.internal.MessageDefault;
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
    private final Config config;

    public CodecByteBuffer(Config config) {
        this.config = config;
    }

    /**
     * 编码
     */
    @Override
    public <T extends BufferWriter> T write(Frame frame, Function<Integer, T> factory) throws IOException {
        if (frame.getMessage() == null) {
            //length (flag + int.bytes)
            int frameSize = Integer.BYTES + Integer.BYTES;
            T target = factory.apply(frameSize);

            //长度
            target.putInt(frameSize);

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
            byte[] metaStringB = frame.getMessage().getMetaString().getBytes(config.getCharset());

            //length (int.bytes + flag + sid + topic + metaString + data + \n*3)
            int frameSize = Integer.BYTES + Integer.BYTES + sidB.length + topicB.length + metaStringB.length + frame.getMessage().getDataSize() + Short.BYTES * 3;

            assertSize("sid", sidB.length, Config.MAX_SIZE_SID);
            assertSize("topic", topicB.length, Config.MAX_SIZE_TOPIC);
            assertSize("metaString", metaStringB.length, Config.MAX_SIZE_META_STRING);
            assertSize("data", frame.getMessage().getDataSize(), Config.MAX_SIZE_FRAGMENT);

            T target = factory.apply(frameSize);

            //长度
            target.putInt(frameSize);

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
            IoUtils.writeTo(frame.getMessage().getData(), target);
            target.flush();

            return target;
        }
    }

    /**
     * 解码
     */
    @Override
    public Frame read(BufferReader buffer) {
        int frameSize = buffer.getInt();

        if (frameSize > (buffer.remaining() + Integer.BYTES)) {
            return null;
        }

        int flag = buffer.getInt();

        if (frameSize == 8) {
            //len + flag
            return new Frame(Flag.Of(flag), null);
        } else {

            int metaBufSize = Math.min(Config.MAX_SIZE_META_STRING, buffer.remaining());

            //1.解码 sid and topic
            ByteBuffer sb = ByteBuffer.allocate(metaBufSize);

            //sid
            String sid = decodeString(buffer, sb, Config.MAX_SIZE_SID);

            //topic
            String topic = decodeString(buffer, sb, Config.MAX_SIZE_TOPIC);

            //metaString
            String metaString = decodeString(buffer, sb, Config.MAX_SIZE_META_STRING);

            //2.解码 body
            int dataRealSize = frameSize - buffer.position();
            byte[] data;
            if (dataRealSize > Config.MAX_SIZE_FRAGMENT) {
                //超界了，空读。必须读，不然协议流会坏掉
                data = new byte[Config.MAX_SIZE_FRAGMENT];
                buffer.get(data, 0, Config.MAX_SIZE_FRAGMENT);
                for (int i = dataRealSize - Config.MAX_SIZE_FRAGMENT; i > 0; i--) {
                    buffer.get();
                }
            } else {
                data = new byte[dataRealSize];
                if (dataRealSize > 0) {
                    buffer.get(data, 0, dataRealSize);
                }
            }

            MessageDefault message = new MessageDefault().sid(sid).topic(topic).entity(new EntityDefault().metaString(metaString).data(data));
            message.flag(Flag.Of(flag));
            return new Frame(message.getFlag(), message);
        }
    }

    protected String decodeString(BufferReader reader, ByteBuffer buf, int maxLen) {
        buf.clear();

        while (true) {
            byte c = reader.get();

            if (c == 10) { //10:'\n'
                break;
            }

            if (maxLen > 0 && maxLen <= buf.position()) {
                //超界了，空读。必须读，不然协议流会坏掉
            } else {
                if (c != 0) { //32:' '
                    buf.put(c);
                }
            }
        }

        buf.flip();
        if (buf.limit() < 1) {
            return "";
        }

        return new String(buf.array(), 0, buf.limit(), config.getCharset());
    }

    private void assertSize(String name, int size, int limitSize) {
        if (size > limitSize) {
            StringBuilder buf = new StringBuilder();
            buf.append("This message ").append(name).append(" size is out of limit ").append(limitSize)
                    .append(" (").append(size).append(")");
            throw new SocketdSizeLimitException(buf.toString());
        }
    }
}