package org.noear.socketd.transport.neta.socket.udp;

import net.hasor.cobble.NumberUtils;
import net.hasor.neta.channel.NetListen;
import net.hasor.neta.channel.NetManager;
import net.hasor.neta.channel.ProtoInitializer;
import net.hasor.neta.channel.SoConfig;
import net.hasor.neta.channel.udp.UdpSoConfig;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.neta.socket.AioServer;
import org.noear.socketd.transport.neta.socket.AioServerConfig;
import org.noear.socketd.transport.server.ServerConfig;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author noear
 * @since 2.3
 */
public class UdpAioServer extends AioServer {
    public UdpAioServer(ServerConfig config) {
        super(config);
    }

    @Override
    public String getTitle() {
        return "udp/aio/neta 1.0/" + SocketD.version();
    }

    @Override
    protected NetListen bindTo(NetManager neta, InetSocketAddress bindAddr, ProtoInitializer initializer) throws IOException {
        UdpSoConfig soConfig;
        if (getConfig() instanceof AioServerConfig) {
            SoConfig config = ((AioServerConfig) getConfig()).getSoConfig();
            if (config instanceof UdpSoConfig) {
                soConfig = (UdpSoConfig) config;
            } else {
                throw new UnsupportedOperationException("only support UdpSoConfig.");
            }
        } else {
            soConfig = SoConfig.UDP();
        }

        soConfig.setRcvPacketSize(NumberUtils.between(getConfig().getMaxUdpSize(), 2048, Constants.MAX_SIZE_FRAME));
        return neta.bind(bindAddr, initializer, soConfig);
    }
}