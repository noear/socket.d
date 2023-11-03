package org.noear.socketd.transport.java_udp;

import org.noear.socketd.transport.java_udp.impl.DatagramFrame;
import org.noear.socketd.transport.java_udp.impl.DatagramTagert;
import org.noear.socketd.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Udp 交换器实现（它没法固定接口，但可以固定输出目录）
 *
 * @author Urara
 * @since 2.0
 */
public class UdpBioChannelAssistant implements ChannelAssistant<DatagramTagert> {
    private static final Logger log = LoggerFactory.getLogger(UdpBioChannelAssistant.class);

    private final Config config;

    public UdpBioChannelAssistant(Config config) {
        this.config = config;
    }

    /**
     * 读取
     */
    public DatagramFrame read(DatagramSocket source) throws IOException {
        //获取第一个包
        DatagramPacket datagramPacket = new DatagramPacket(new byte[config.getMaxUdpSize()], config.getMaxUdpSize());
        source.receive(datagramPacket);
        if (datagramPacket.getLength() < Integer.BYTES) {
            return null;
        }

        //获取数据（接着在原地址上拿）
        ByteBuffer byteBuffer = ByteBuffer.wrap(datagramPacket.getData(), 0, datagramPacket.getLength());
        byteBuffer.mark();

        int frameSize = byteBuffer.getInt();
        if (frameSize > datagramPacket.getLength()) {
            return null;
        }
        byteBuffer.reset();

        Frame frame = config.getCodec().decode(byteBuffer);

        return new DatagramFrame(datagramPacket, frame);
    }

    /**
     * 写入
     */
    @Override
    public void write(DatagramTagert target, Frame frame) throws IOException {
        byte[] dataBytes = config.getCodec().encode(frame).array();

        //byte[] sizeBytes = ByteBuffer.allocate(Integer.BYTES).putInt(dataBytes.length).array();

        //先发长度包
        //target.send(sizeBytes);
        //再发数据包
        target.send(dataBytes);
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
