package org.noear.socketd.transport.java_udp;

import org.noear.socketd.transport.core.ChannelAssistant;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Config;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.codec.ByteBufferCodecReader;
import org.noear.socketd.transport.core.codec.ByteBufferCodecWriter;
import org.noear.socketd.transport.java_udp.impl.DatagramFrame;
import org.noear.socketd.transport.java_udp.impl.DatagramTagert;
import org.noear.socketd.utils.IoCompletionHandler;
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
        //接收
        DatagramPacket datagramPacket = new DatagramPacket(new byte[config.getMaxUdpSize()], config.getMaxUdpSize());
        source.receive(datagramPacket);
        if (datagramPacket.getLength() < Integer.BYTES) {
            return null;
        }

        //包装
        ByteBuffer buffer = ByteBuffer.wrap(datagramPacket.getData(), 0, datagramPacket.getLength());
        buffer.mark();

        int frameSize = buffer.getInt();
        if (frameSize > datagramPacket.getLength()) {
            return null;
        }

        buffer.reset();

        Frame frame = config.getCodec().read(new ByteBufferCodecReader(buffer));

        return new DatagramFrame(datagramPacket, frame);
    }

    /**
     * 写入
     */
    @Override
    public void write(DatagramTagert target, Frame frame, ChannelInternal channel, IoCompletionHandler completionHandler) {
        try {
            ByteBufferCodecWriter writer = config.getCodec().write(frame, i -> new ByteBufferCodecWriter(ByteBuffer.allocate(i)));
            target.send(writer.getBuffer().array());

            completionHandler.completed(true, null);
        } catch (Throwable e) {
            completionHandler.completed(false, e);
        }
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
