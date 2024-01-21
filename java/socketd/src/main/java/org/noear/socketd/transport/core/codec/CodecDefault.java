package org.noear.socketd.transport.core.codec;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.transport.core.entity.MessageBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * 编解码器默认实现
 *
 * @author noear
 * @since 2.0
 */
public class CodecDefault implements Codec {
    private final Config config;

    public CodecDefault(Config config) {
        this.config = config;
    }

    /**
     * 编码
     */
    @Override
    public <T extends CodecWriter> T write(Frame frame, Function<Integer, T> writerFactory) throws IOException {
        if (frame.message() == null) {
            //length (len[int] + flag[int])
            int frameSize = Integer.BYTES + Integer.BYTES;
            T writer = writerFactory.apply(frameSize);

            //长度
            writer.putInt(frameSize);

            //flag
            writer.putInt(frame.flag());
            writer.flush();

            return writer;
        } else {
            //sid
            byte[] sidB = frame.message().sid().getBytes(config.getCharset());
            //event
            byte[] eventB = frame.message().event().getBytes(config.getCharset());
            //metaString
            byte[] metaStringB = frame.message().metaString().getBytes(config.getCharset());

            //length (len[int] + flag[int] + sid + event + metaString + data + \n*3)
            int frameSize = Integer.BYTES + Integer.BYTES + sidB.length + eventB.length + metaStringB.length + frame.message().dataSize() + Short.BYTES * 3;

            Asserts.assertSize("sid", sidB.length, Constants.MAX_SIZE_SID);
            Asserts.assertSize("event", eventB.length, Constants.MAX_SIZE_EVENT);
            Asserts.assertSize("metaString", metaStringB.length, Constants.MAX_SIZE_META_STRING);
            Asserts.assertSize("data", frame.message().dataSize(), Constants.MAX_SIZE_DATA);

            T writer = writerFactory.apply(frameSize);

            //长度
            writer.putInt(frameSize);

            //flag
            writer.putInt(frame.flag());

            //sid
            writer.putBytes(sidB);
            writer.putChar('\n');

            //event
            writer.putBytes(eventB);
            writer.putChar('\n');

            //metaString
            writer.putBytes(metaStringB);
            writer.putChar('\n');

            //data
            writer.putBytes(frame.message().dataAsBytes());

            writer.flush();

            return writer;
        }
    }

    /**
     * 解码
     */
    @Override
    public Frame read(CodecReader reader) {
        int frameSize = reader.getInt();

        if (frameSize > (reader.remaining() + Integer.BYTES)) {
            //如果未满，等
            return null;
        }

        if (frameSize > Constants.MAX_SIZE_FRAME) {
            //如果超界，跳
            reader.skipBytes(frameSize - Integer.BYTES);
            return null;
        }

        int flag = reader.getInt();

        if (frameSize == 8) {
            //len[int] + flag[int]
            return new Frame(Flags.of(flag), null);
        } else {

            int metaBufSize = Math.min(Constants.MAX_SIZE_META_STRING, reader.remaining());

            //1.解码 sid and event
            ByteBuffer buf = ByteBuffer.allocate(metaBufSize);

            //sid
            String sid = decodeString(reader, buf, Constants.MAX_SIZE_SID);

            //event
            String event = decodeString(reader, buf, Constants.MAX_SIZE_EVENT);

            //metaString
            String metaString = decodeString(reader, buf, Constants.MAX_SIZE_META_STRING);

            //2.解码 body
            int dataRealSize = frameSize - reader.position();
            byte[] data;
            if (dataRealSize > Constants.MAX_SIZE_DATA) {
                //超界了，空读。必须读，不然协议流会坏掉
                data = new byte[Constants.MAX_SIZE_DATA];
                reader.getBytes(data, 0, Constants.MAX_SIZE_DATA);
                for (int i = dataRealSize - Constants.MAX_SIZE_DATA; i > 0; i--) {
                    reader.getByte();
                }
            } else {
                data = new byte[dataRealSize];
                if (dataRealSize > 0) {
                    reader.getBytes(data, 0, dataRealSize);
                }
            }

            //先 data , 后 metaString (避免 data 时修改元信息)
            MessageInternal message = new MessageBuilder()
                    .flag(Flags.of(flag))
                    .sid(sid)
                    .event(event)
                    .entity(new EntityDefault().dataSet(data).metaStringSet(metaString))
                    .build();

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
    protected String decodeString(CodecReader reader, ByteBuffer buf, int maxLen) {
        buf.clear();
        while (true) {
            byte c = reader.getByte();

            if (c == 0 && reader.peekByte() == 10) { //x0a:'\n'
                reader.skipBytes(1);
                break;
            }

            if (maxLen > 0 && maxLen <= buf.position()) {
                //超界了，空读。必须读，不然协议流会坏掉
            } else {
                buf.put(c);
            }
        }

        buf.flip();
        if (buf.limit() < 1) {
            return "";
        }

        return new String(buf.array(), 0, buf.limit(), config.getCharset());
    }
}