package org.noear.socketd.broker.bio.client;

import org.noear.socketd.protocol.Listener;
import org.noear.socketd.protocol.Session;
import org.noear.socketd.client.Connector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;

/**
 * @author noear 2023/10/13 created
 */
public class BioConnector implements Connector {
    BioClientConfig clientConfig;
    Listener listener;
    URI uri;

    public BioConnector(String uriStr){
        this.uri = URI.create(uriStr);
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public boolean autoReconnect() {
        return false;
    }

    @Override
    public void listen(Listener listener) {
        this.listener = listener;
    }

    @Override
    public Session open() throws IOException {
        SocketAddress socketAddress = new InetSocketAddress(uri().getHost(), uri().getPort());
        Socket socket = new Socket();

        if (clientConfig.getSocketTimeout() > 0) {
            socket.setSoTimeout(clientConfig.getSocketTimeout());
        }

        if (clientConfig.getConnectTimeout() > 0) {
            socket.connect(socketAddress, clientConfig.getConnectTimeout());
        } else {
            socket.connect(socketAddress);
        }

        BioClientSession session = new BioClientSession(socket);

        session.start();

        return session;
    }

    void startReceive(Session session, Socket socket) {

    }
}
