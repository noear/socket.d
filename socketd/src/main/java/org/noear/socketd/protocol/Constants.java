package org.noear.socketd.protocol;

/**
 * 常量
 *
 * @author noear
 * @since 2.0
 */
public class Constants {
    public static final String HEARDER_SOCKETD_VERSION = "SocketD-Version";
    public static final String HEARDER_SOCKETD_PROTOCOLS = "SocketD-Protocols";

    public static final String HEARDER_CONNECT = "SocketD-Version=2.0";
    public static final String HEARDER_CONNACK = "SocketD-Version=2.0";


    public static final String DEF_KEY = "";
    public static final String DEF_TOPIC = "";
    public static final String DEF_META_STRING = "";
    public static final byte[] DEF_DATA = new byte[]{};
}
