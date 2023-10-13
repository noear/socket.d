package org.noear.socketd.protocol;

/**
 * @author noear 2023/10/13 created
 */
public interface Handshaker {
    String getUri(); //::1.2.3.4/a/b
    String[] getProtocols(); //::[http,ws] ; [tcp,sd]; [tcp,http,ws,sd]
    String getVersion();
}
