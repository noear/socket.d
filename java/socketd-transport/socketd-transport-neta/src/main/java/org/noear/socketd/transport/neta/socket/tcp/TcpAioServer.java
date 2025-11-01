package org.noear.socketd.transport.neta.socket.tcp;

import net.hasor.neta.channel.NetListen;
import net.hasor.neta.channel.NetManager;
import net.hasor.neta.channel.ProtoInitializer;
import net.hasor.neta.channel.SoConfig;
import net.hasor.neta.channel.tcp.TcpSoConfig;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.neta.socket.AioServer;
import org.noear.socketd.transport.neta.socket.AioServerConfig;
import org.noear.socketd.transport.server.ServerConfig;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author noear
 * @since 2.3
 */
public class TcpAioServer extends AioServer {
    public TcpAioServer(ServerConfig config) {
        super(config);
    }

    @Override
    public String getTitle() {
        return "tcp/aio/neta 1.0/" + SocketD.version();
    }

    @Override
    protected NetListen bindTo(NetManager neta, InetSocketAddress bindAddr, ProtoInitializer initializer) throws IOException {
        TcpSoConfig soConfig;
        if (getConfig() instanceof AioServerConfig) {
            SoConfig config = ((AioServerConfig) getConfig()).getSoConfig();
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
        soConfig.setSoKeepAlive(false);
        return neta.bind(bindAddr, initializer, soConfig);
    }
}