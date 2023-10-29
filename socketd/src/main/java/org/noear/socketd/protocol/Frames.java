package org.noear.socketd.protocol;

import org.noear.socketd.protocol.impl.EntityBuilder;
import org.noear.socketd.protocol.impl.PayloadDefault;
import org.noear.socketd.utils.Utils;

/**
 * 帧工厂
 *
 * @author noear
 * @since 2.0
 * */
public class Frames {
    public static final Frame connectFrame(String uri) {
        return new Frame(Flag.Connect, new PayloadDefault().key(Utils.guid()).topic(uri).entity(new EntityBuilder().header(Constants.HEARDER_CONNECT)));
    }

    public static final Frame connackFrame(Payload connect) {
        return new Frame(Flag.Connack, new PayloadDefault().key(connect.getKey()).entity(new EntityBuilder().header(Constants.HEARDER_CONNACK)));
    }

    public static final Frame pingFrame() {
        return new Frame(Flag.Ping, null);
    }

    public static final Frame pongFrame() {
        return new Frame(Flag.Pong, null);
    }
}
