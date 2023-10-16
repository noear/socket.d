package org.noear.socketd.protocol.impl;

import org.noear.socketd.protocol.Constants;
import org.noear.socketd.protocol.Flag;
import org.noear.socketd.protocol.Frame;
import org.noear.socketd.protocol.Payload;

/**
 *
 * @author noear
 * @since 2.0
 * */
public class FrameFactory {
    public static final Frame connectFrame(String uri) {
        return new Frame(Flag.Connect, new Payload(uri, Constants.HEARDER_CONNECT));
    }

    public static final Frame connackFrame() {
        return new Frame(Flag.Connack, new Payload(Constants.HEARDER_CONNACK));
    }

    public static final Frame pingFrame() {
        return new Frame(Flag.Ping, null);
    }

    public static final Frame pongFrame() {
        return new Frame(Flag.Pong, null);
    }
}
