package org.noear.socketd.transport.netty.udp.impl;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
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
    private final ChannelHandlerContext ctx;
    private final DatagramPacket packet;

    public DatagramTagert(ChannelHandlerContext ctx, DatagramPacket packet, boolean isClient) {
        this.ctx = ctx;
        this.packet = packet;
        this.isClient = isClient;
    }


    public void send(byte[] bytes) throws IOException {
        if (isClient) {
            ctx.channel().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bytes), (InetSocketAddress) ctx.channel().remoteAddress()));
        } else {
            ctx.channel().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bytes), packet.sender()));
        }
    }

    public InetSocketAddress getRemoteAddress() {
        if (isClient) {
            return (InetSocketAddress) ctx.channel().remoteAddress();
        } else {
            return packet.sender();
        }
    }

    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) ctx.channel().localAddress();
    }


    @Override
    public void close() throws IOException {
        if (isClient) {
            ctx.close();
        }
    }
}
