package org.noear.socketd.protocol;/**
 *
 * @author noear 2023/10/14 created
 * */
public class Frames {
    public static final Frame pingConnack = new Frame(Flag.Connack, new Payload("","",Constants.HEARDER_CONNACK,new byte[]{}));
    public static final Frame pingFrame = new Frame(Flag.Ping, null);
    public static final Frame pongFrame = new Frame(Flag.Pong, null);
}
