package org.noear.socketd.broker.netty.udp.impl;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Udp 通道目标
 *
 * @author Urara
 * @since 2.0
 */
public class DatagramTagert implements Closeable {
    private final boolean isClient;
    private final Channel socket;
    private final DatagramPacket packet;
    public DatagramTagert(Channel socket, DatagramPacket packet, boolean isClient){
        this.socket = socket;
        this.packet = packet;
        this.isClient = isClient;
    }


    public void send(byte[] bytes) throws IOException {
        if (isClient) {
            socket.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bytes), (InetSocketAddress)socket.remoteAddress()));
        } else {
            socket.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bytes), packet.sender()));
        }
    }

    public InetSocketAddress getRemoteAddress() {
        if (isClient) {
            return (InetSocketAddress) socket.remoteAddress();
        } else {
            return (InetSocketAddress) packet.sender();
        }
    }

    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) socket.localAddress();
    }


    @Override
    public void close() throws IOException {
        if (isClient) {
            socket.close();
        }
    }
}
