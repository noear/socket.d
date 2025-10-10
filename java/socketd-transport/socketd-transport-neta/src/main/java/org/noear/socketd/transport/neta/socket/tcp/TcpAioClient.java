package org.noear.socketd.transport.neta.socket.tcp;

import net.hasor.cobble.concurrent.future.Future;
import net.hasor.neta.channel.NetChannel;
import net.hasor.neta.channel.NetManager;
import net.hasor.neta.channel.ProtoInitializer;
import net.hasor.neta.channel.SoConfig;
import net.hasor.neta.channel.tcp.TcpSoConfig;
import org.noear.socketd.transport.client.ClientConfig;
import org.noear.socketd.transport.client.ClientConnector;
import org.noear.socketd.transport.neta.socket.AioClient;
import org.noear.socketd.transport.neta.socket.AioClientConfig;
import org.noear.socketd.transport.neta.socket.AioClientConnector;

import java.net.InetSocketAddress;

/**
 * @author noear
 * @since 2.3
 */
public class TcpAioClient extends AioClient {
    public TcpAioClient(ClientConfig clientConfig) {
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
        TcpSoConfig soConfig;
        if (getConfig() instanceof AioClientConfig) {
            SoConfig config = ((AioClientConfig) getConfig()).getSoConfig();
            if (config instanceof TcpSoConfig) {
                soConfig = (TcpSoConfig) config;
            } else {
                throw new UnsupportedOperationException("only support TcpSoConfig.");
            }
        } else {
            soConfig = SoConfig.TCP();
        }

        soConfig.setRcvSlotSize(getConfig().getReadBufferSize());
        soConfig.setSndSlotSize(getConfig().getWriteBufferSize());

        return neta.connectAsync(remoteAddr, initializer, soConfig);
    }
}
