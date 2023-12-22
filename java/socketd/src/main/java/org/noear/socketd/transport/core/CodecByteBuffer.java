package org.noear.socketd.transport.core;

import org.noear.socketd.transport.core.buffer.BufferReader;
import org.noear.socketd.transport.core.buffer.BufferWriter;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.transport.core.internal.MessageDefault;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * 编解码器（基于 ByteBuffer 编解）
 *
 * @author noear
 * @since 2.0
 */
public class CodecByteBuffer implements Codec {
    private final Config config;

    public CodecByteBuffer(Config config) {
        this.config = config;
    }

    /**
     * 编码
     */
    @Override
    public <T extends BufferWriter> T write(Frame frame, Function<Integer, T> targetFactory) throws IOException {
        if (frame.getMessage() == null) {
            //length (len[int] + flag[int])
            int frameSize = Integer.BYTES + Integer.BYTES;
            T target = targetFactory.apply(frameSize);

            //长度
            target.putInt(frameSize);

            //flag
            target.putInt(frame.getFlag());
            target.flush();

            return target;
        } else {
            //sid
            byte[] sidB = frame.getMessage().sid().getBytes(config.getCharset());
            //event
            byte[] eventB = frame.getMessage().event().getBytes(config.getCharset());
            //metaString
            byte[] metaStringB = frame.getMessage().metaString().getBytes(config.getCharset());

            //length (len[int] + flag[int] + sid + event + metaString + data + \n*3)
            int frameSize = Integer.BYTES + Integer.BYTES + sidB.length + eventB.length + metaStringB.length + frame.getMessage().dataSize() + Short.BYTES * 3;

            Asserts.assertSize("sid", sidB.length, Constants.MAX_SIZE_SID);
            Asserts.assertSize("event", eventB.length, Constants.MAX_SIZE_EVENT);
            Asserts.assertSize("metaString", metaStringB.length, Constants.MAX_SIZE_META_STRING);
            Asserts.assertSize("data", frame.getMessage().dataSize(), Constants.MAX_SIZE_DATA);

            T target = targetFactory.apply(frameSize);

            //长度
            target.putInt(frameSize);

            //flag
            target.putInt(frame.getFlag());

            //sid
            target.putBytes(sidB);
            target.putChar('\n');

            //event
            target.putBytes(eventB);
            target.putChar('\n');

            //metaString
            target.putBytes(metaStringB);
            target.putChar('\n');

            //data
            target.putBytes(frame.getMessage().dataAsBytes());

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
            //len[int] + flag[int]
            return new Frame(Flags.of(flag), null);
        } else {

            int metaBufSize = Math.min(Constants.MAX_SIZE_META_STRING, buffer.remaining());

            //1.解码 sid and event
            ByteBuffer sb = ByteBuffer.allocate(metaBufSize);

            //sid
            String sid = decodeString(buffer, sb, Constants.MAX_SIZE_SID);

            //event
            String event = decodeString(buffer, sb, Constants.MAX_SIZE_EVENT);

            //metaString
            String metaString = decodeString(buffer, sb, Constants.MAX_SIZE_META_STRING);

            //2.解码 body
            int dataRealSize = frameSize - buffer.position();
            byte[] data;
            if (dataRealSize > Constants.MAX_SIZE_DATA) {
                //超界了，空读。必须读，不然协议流会坏掉
                data = new byte[Constants.MAX_SIZE_DATA];
                buffer.get(data, 0, Constants.MAX_SIZE_DATA);
                for (int i = dataRealSize - Constants.MAX_SIZE_DATA; i > 0; i--) {
                    buffer.get();
                }
            } else {
                data = new byte[dataRealSize];
                if (dataRealSize > 0) {
                    buffer.get(data, 0, dataRealSize);
                }
            }

            //先 data , 后 metaString (避免 data 时修改元信息)
            MessageDefault message = new MessageDefault()
                    .flag(Flags.of(flag))
                    .sid(sid)
                    .event(event)
                    .entity(new EntityDefault().data(data).metaString(metaString));

            return new Frame(message.flag(), message);
        }
    }

    /**
     * 解码时，以换行符为间隔
     *
     * @param reader 读取器
     * @param buf    复用缓冲
     * @param maxLen 最大长度
     */
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
}