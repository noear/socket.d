package org.noear.socketd.broker.java_udp.impl;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * @author noear
 * @since 2.0
 */
public class DatagramTagert implements Closeable {
    private final boolean isClient;
    private final DatagramSocket socket;
    private final DatagramPacket packet;
    public DatagramTagert(DatagramSocket socket, DatagramPacket packet, boolean isClient){
        this.socket = socket;
        this.packet = packet;
        this.isClient = isClient;
    }


    public void send(byte[] bytes) throws IOException {
        if (isClient) {
            socket.send(new DatagramPacket(bytes, bytes.length, socket.getRemoteSocketAddress()));
        } else {
            socket.send(new DatagramPacket(bytes, bytes.length, packet.getSocketAddress()));
        }
    }

    public InetSocketAddress getRemoteAddress() throws IOException {
        if (isClient) {
            return (InetSocketAddress) socket.getRemoteSocketAddress();
        } else {
            return (InetSocketAddress) packet.getSocketAddress();
        }
    }

    public InetSocketAddress getLocalAddress() throws IOException {
        return (InetSocketAddress) socket.getLocalSocketAddress();
    }


    @Override
    public void close() throws IOException {
        if (isClient) {
            socket.close();
        }
    }
}
