package org.noear.socketd.broker.bio.server;

import org.noear.socketd.protocol.Frame;
import org.noear.socketd.protocol.ChannelReceiver;
import org.noear.socketd.protocol.codec.DecoderByteBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author noear 2023/10/14 created
 */
public class BioChannelReceiver implements ChannelReceiver<InputStream> {
    DecoderByteBuffer decoderByteBuffer = new DecoderByteBuffer();

    @Override
    public Frame receive(InputStream input) throws IOException {
        if (input == null) {
            return null;
        }

        byte[] lenBts = new byte[4];
        if (input.read(lenBts) < -1) {
            return null;
        }

        int len = bytesToInt32(lenBts);

        if (len == 0) {
            return null;
        }

        ByteBuffer buffer = ByteBuffer.allocate(len);
        buffer.putInt(len);

        int bufSize = 512;
        byte[] buf = new byte[bufSize];

        int readSize = 0;

        while (true) {
            if (buffer.remaining() > bufSize) {
                readSize = bufSize;
            } else {
                readSize = buffer.remaining();
            }

            if ((readSize = input.read(buf, 0, readSize)) > 0) {
                buffer.put(buf, 0, readSize);
            } else {
                break;
            }
        }

        buffer.flip();

        return decoderByteBuffer.decode(buffer);
    }

    private static int bytesToInt32(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (3 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }
}
