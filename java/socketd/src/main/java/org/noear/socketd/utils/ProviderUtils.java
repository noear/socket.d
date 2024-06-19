package org.noear.socketd.utils;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientProvider;
import org.noear.socketd.transport.server.ServerProvider;

/**
 * 提供者工具
 *
 * @author noear
 * @since 2.5
 */
public class ProviderUtils {
    /**
     * 自动弱加载
     */
    public static void autoWeakLoad() {
        String[] _providers = new String[]{
                "org.noear.socketd.transport.netty.tcp.TcpNioProvider",
                "org.noear.socketd.transport.netty.udp.UdpNioProvider",
                "org.noear.socketd.transport.java_websocket.WsNioProvider",
                "org.noear.socketd.transport.smartsocket.tcp.TcpAioProvider",
                "org.noear.socketd.transport.java_kcp.KcpNioProvider",
                "org.noear.socketd.transport.java_tcp.TcpBioProvider",
                "org.noear.socketd.transport.java_udp.UdpBioProvider"
        };

        for (String p1 : _providers) {
            try {
                Class<?> clz = SocketD.class.getClassLoader().loadClass(p1);
                if (clz == null) {
                    continue;
                }

                Object obj = clz.getDeclaredConstructor().newInstance();

                if (obj instanceof ClientProvider) {
                    SocketD.registerClientProvider((ClientProvider) obj);
                }

                if (obj instanceof ServerProvider) {
                    SocketD.registerServerProvider((ServerProvider) obj);
                }
            } catch (Throwable e) {
                //乎略
            }
        }
    }
}