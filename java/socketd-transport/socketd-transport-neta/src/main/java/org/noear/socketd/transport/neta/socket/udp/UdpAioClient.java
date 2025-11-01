package org.noear.socketd.transport.neta.socket.udp;

import net.hasor.cobble.NumberUtils;
import net.hasor.cobble.concurrent.future.Future;
import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.NetManager;
import net.hasor.neta.channel.ProtoInitializer;
import net.hasor.neta.channel.SoConfig;
import net.hasor.neta.channel.udp.UdpSoConfig;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientConnector;
import org.noear.socketd.transport.core.Constants;
import org.noear.socketd.transport.neta.socket.AioClient;
import org.noear.socketd.transport.neta.socket.AioClientConfig;
import org.noear.socketd.transport.neta.socket.AioClientConnector;

import java.net.InetSocketAddress;

/**
 * @author noear
 * @since 2.3
 */
public class UdpAioClient extends AioClient {
    public UdpAioClient(ClientConfig clientConfig) {
        super(clientConfig);
    }

    @Override
    protected ClientConnector createConnector() {
        return new AioClientConnector(this) {
            @Override
            protected Future<NetChannel> connectTo(NetManager neta, InetSocketAddress remoteAddr, ProtoInitializer initializer) {
                return tcpConnectTo(neta, remoteAddr, initializer);
            }
        };
    }

    protected Future<NetChannel> tcpConnectTo(NetManager neta, InetSocketAddress remoteAddr, ProtoInitializer initializer) {
        UdpSoConfig soConfig;
        if (getConfig() instanceof AioClientConfig) {
            SoConfig config = ((AioClientConfig) getConfig()).getSoConfig();
            if (config instanceof UdpSoConfig) {
                soConfig = (UdpSoConfig) config;
            } else {
                throw new UnsupportedOperationException("only support UdpSoConfig.");
            }
        } else {
            soConfig = SoConfig.UDP();
        }

        soConfig.setRcvPacketSize(NumberUtils.between(getConfig().getMaxUdpSize(), 2048, Constants.MAX_SIZE_FRAME));
        return neta.connectAsync(remoteAddr, initializer, soConfig);
    }
}
