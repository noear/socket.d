package org.noear.socketd.broker.java_udp;

import org.noear.socketd.broker.java_udp.impl.DatagramFrame;
import org.noear.socketd.broker.java_udp.impl.DatagramTagert;
import org.noear.socketd.protocol.ChannelAssistant;
import org.noear.socketd.protocol.CodecByteBuffer;
import org.noear.socketd.protocol.Constants;
import org.noear.socketd.protocol.Frame;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Udp-Bio 交换器实现（它没法固定接口，但可以固定输出目录）
 *
 * @author noear
 * @since 2.0
 */
public class UdpBioChannelAssistant implements ChannelAssistant<DatagramTagert> {
    private CodecByteBuffer codec = new CodecByteBuffer();

    public int readInt32(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (3 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }

    /**
     * 读取
     */
    public DatagramFrame read(DatagramSocket source) throws IOException {
        //获取长度
        DatagramPacket datagramPacket = new DatagramPacket(new byte[Constants.LEN_INT], Constants.LEN_INT);
        source.receive(datagramPacket);
        byte[] sizeBytes = datagramPacket.getData();
        if (sizeBytes == null || sizeBytes.length == 0) {
            return null;
        }

        //获取数据（接着在原地址上拿）
        int size = readInt32(sizeBytes) - Constants.LEN_INT;
        datagramPacket = new DatagramPacket(new byte[size], size, datagramPacket.getSocketAddress());
        source.receive(datagramPacket);
        byte[] dataBytes = datagramPacket.getData();
        if (dataBytes == null || dataBytes.length == 0) {
            return null;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(size + Constants.LEN_INT);
        byteBuffer.put(sizeBytes);
        byteBuffer.put(dataBytes);

        Frame frame = codec.decode(byteBuffer);

        return new DatagramFrame(datagramPacket, frame);
    }

    /**
     * 写入
     */
    @Override
    public void write(DatagramTagert target, Frame frame) throws IOException {
        byte[] bytes = codec.encode(frame).array();
        target.send(bytes);
    }

    @Override
    public boolean isValid(DatagramTagert target) {
        return true;
    }

    @Override
    public void close(DatagramTagert target) throws IOException {
        target.close();
    }

    @Override
    public InetSocketAddress getRemoteAddress(DatagramTagert target) throws IOException {
        return target.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress(DatagramTagert target) throws IOException {
        return target.getLocalAddress();
    }
}
