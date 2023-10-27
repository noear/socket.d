package org.noear.socketd.protocol;

import org.noear.socketd.utils.Utils;

/**
 * 帧工厂
 * @author noear
 * @since 2.0
 * */
public class Frames {
    public static final Frame connectFrame(String uri) {
        return new Frame(Flag.Connect, new Payload(Utils.guid(), uri, Constants.HEARDER_CONNECT));
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
