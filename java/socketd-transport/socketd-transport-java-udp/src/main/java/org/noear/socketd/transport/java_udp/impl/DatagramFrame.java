package org.noear.socketd.transport.java_udp.impl;

import org.noear.socketd.transport.core.Frame;

import java.net.DatagramPacket;

/**
 * Udp 帖
 *
 * @author Urara
 * @since 2.0
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
