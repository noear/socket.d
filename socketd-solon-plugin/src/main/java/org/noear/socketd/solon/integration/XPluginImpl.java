package org.noear.socketd.solon.integration;

import org.noear.socketd.SocketD;
import org.noear.socketd.client.Client;
import org.noear.socketd.protocol.Listener;
import org.noear.socketd.server.Server;
import org.noear.socketd.server.ServerConfig;
import org.noear.socketd.solon.annotation.SocketdClient;
import org.noear.socketd.solon.annotation.SocketdServer;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 2.0
 */
public class XPluginImpl implements Plugin {
    List<Server> serverList = new ArrayList<>();
    List<Client> clientList = new ArrayList<>();

    @Override
    public void start(AppContext context) throws Throwable {
        context.beanBuilderAdd(SocketdServer.class, (clz, bw, anno) -> {
            if (bw.raw() instanceof Listener) {
                for (String s : anno.schema()) {
                    Server server = SocketD.createServer(new ServerConfig(s));
                    server.listen(bw.raw());
                    serverList.add(server);

                    //通过事件扩展
                    EventBus.publish(server);
                }
            }
        });

        context.beanBuilderAdd(SocketdClient.class, (clz, bw, anno) -> {
            Client client = SocketD.createClient(anno.url());
            client.config(options -> options
                    .autoReconnect(anno.autoReconnect())
                    .heartbeatInterval(anno.heartbeatRate() * 1000));

            clientList.add(client);
        });

        context.lifecycle(() -> {
            for (Server server : serverList) {
                server.start();
            }

            for(Client client : clientList){
                client.open();
            }
        });
    }
}