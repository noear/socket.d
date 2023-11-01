package org.noear.socketd.broker.java_udp.impl;

import org.noear.socketd.protocol.Frame;

import java.net.DatagramPacket;

/**
 * @author noear
 * @since 2.5
 */
public class DatagramFrame {
    private DatagramPacket packet;
    private Frame frame;

    public DatagramFrame(DatagramPacket packet, Frame frame) {
        this.packet = packet;
        this.frame = frame;
    }

    public DatagramPacket getPacket() {
        return packet;
    }

    public String getPacketAddress() {
        return packet.getAddress().toString() + ":" + packet.getPort();
    }

    public Frame getFrame() {
        return frame;
    }
}
